package com.lin.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by lin on 2018/6/30.
 * 遍历IP和端口进行扫描，将结果写回UI
 */
public class TCPScan extends Thread {

    private final Logger logger = LoggerFactory.getLogger(TCPScan.class);

    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
    }


    static final int ADDRESS_SCAN = 0,
                     DOMAINNAME_SCAN = 1;

    // 查询方式：ADDRESS_SCAN为根据IP地址；NAME_SCAN为根据域名扫描
    static int scanType = DOMAINNAME_SCAN;

    /*
     *   IP地址段
     *   -- secIP[1], secIP[2], secIP[3]分别对应IPv4的前三段
     *   -- secIP[3], secIP[4]都对应IPv4的第四段, 但前者表示起始地址,后者表示结束地址.
     */
    static int[] secIP = new int[5];

    // 域名
    static String hostName = "localhost";

    /*
     * 端口号数组
     * -- port[0]为起始端口, port[1]为终止端口
     */
    static int[] port = new int[2];


    // 最大启动线程数
    static int numOfThreads;

    // 每个线程独立的起始端口偏移量
    private int offsetPort = 0;

    // 端口和对应服务的Map
    private static Map<Integer, String> portForService = new TreeMap<>();

    // 主机的InetAddress实例对象
    private static InetAddress host;

    public TCPScan(String name, int offsetPort) {
        super(name);
        this.offsetPort = offsetPort;
    }

    /*
        运行方法
     */
    @Override
    public void run() {

        // 根据IP地址进行扫描
        if (scanType == ADDRESS_SCAN) {

            // IP地址循环扫描
            for (int i = secIP[3]; i <= secIP[4]; i++) { //遍历需要扫描的 IP 范围

                String ip = "";
                // 完整的IP地址
                for (int j = 0; j < 3; j++)
                    ip += secIP[j] + ".";
                ip += i;//遍历IP范围内的所有IP地址

                try {
                    host = InetAddress.getByAddress(parseIPv4Address(ip));
                } catch (UnknownHostException e) {
                    logger.debug("不可知的主机异常");
                    e.printStackTrace();
                }
                // 判定host的端口是否可访问(未超时, 默认1000ms), 如果是则进行扫描
                portScan(host);
            }
            return;
        }

        // 根据域名进行扫描
        if (scanType == DOMAINNAME_SCAN) {

            try {
                host = InetAddress.getByName(hostName); // "newhost"和"Coder"都是可选域名.
            } catch (UnknownHostException e) {
                logger.debug("不可知的主机异常");
                e.printStackTrace();
            }
            // 判定host的端口是否可访问(未超时, 默认1000ms), 如果是则进行扫描
            portScan(host);

            return;
        }
    }

    /**
     * 根据给定的InetAddress实例对象host以及输出结果的StringBuilder实例对象out,
     * 对目标主机beginPort至endPort范围内的端口(并不是全部)进行扫描.
     * 注意: 为了实现多线程处理, 对每个线程的起始端口和端口递增数分别进行了处理.
     * 即:
     * 起始端口为beginPort + 端口偏移量;
     * 端口递增为 += 总线程数.
     *
     * 主机的InetAddress实例, 可通过InetAddress.getByName(String host)或
     *             InetAddress.getByAddress(byte[] addr)获得.
     */
    private void portScan(InetAddress host) {
        Socket theTCPSocket;
        // 端口扫描, 每个线程实例拥有一个端口扫描的偏移量, 以实现端口的并发(分组)扫描.
        for (int j = port[0] + offsetPort; j <= port[1]; j += numOfThreads) {
            try {
                // 初始化目标主机的套接字
                theTCPSocket = new Socket(host,j);
                theTCPSocket.close();
                ThreadScan.resultTextArea.append(host.getCanonicalHostName()+":"+j+"   可用\n");

                // 日志记录host.getCanonicalHostName(), j
               /* logger.info("{}:{}:{}--{}", host, j,
                        portForService.containsKey(j) ? portForService.get(j) : "(UNKNOWN_SERVICE)",
                        this.toString());*/
                logger.debug("端口："+j+" 可用",host.getCanonicalHostName(), j);
            } catch (SocketException e) {
                logger.debug("{}:{}不可访问",host.getCanonicalHostName(),j);
                //logger.debug("端口："+j+" 不可用",host.getCanonicalHostName(), j);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (j == port[1]) {
                logger.info("扫描完成");
                ThreadScan.resultTextArea.append("扫描完成.\n");

                JOptionPane.showMessageDialog(ThreadScan.main, "扫描完成", "信息", JOptionPane.INFORMATION_MESSAGE);
                if (!ThreadScan.submitBtn.isEnabled())
                    ThreadScan.submitBtn.setEnabled(true);
            }
        }
    }

    /**
     通过字符串解析IP
     */
    private byte[] parseIPv4Address(String addr) {
        String[] addrStr = addr.split("\\D+");
        int[] addrInt = new int[addrStr.length];
        for (int i = 0; i < addrStr.length; i++) {
            addrInt[i] = Integer.parseInt(addrStr[i]);
        }

        return parseIP4Address(addrInt);
    }

    /**
     * 解析出符合InetAddress.getByAddress(byte[] addr)要求的byte[]数组
     *
     * @param addr 长度为4，值为0-255的int数组
     * @return InetAddress.getByAddress(byte[] addr)可接受的byte数组
     */
    private byte[] parseIP4Address(int[] addr) { //ip解析
        if (addr.length > 4)
            throw new IllegalArgumentException("IP地址应为4个整数");

        byte[] ip = new byte[4];

        for (int i = 0; i < addr.length; i++) {
            if (addr[i] < 0 || addr[i] > 255)
                throw new IllegalArgumentException("IP地址应为0至255的整数");

            ip[i] = (byte) addr[i];
        }
        return ip;
    }

    @Override
    public String toString() {
        return "ThreadName:" + this.getName();
    }
}