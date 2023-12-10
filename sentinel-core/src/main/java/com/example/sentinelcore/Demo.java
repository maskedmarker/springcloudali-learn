package com.example.sentinelcore;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 启动时加入 JVM 参数 -Dcsp.sentinel.dashboard.server=consoleIp:port 指定控制台地址和端口。
 * 若启动多个应用，则需要通过 -Dcsp.sentinel.api.port=xxxx 指定客户端监控 API 的端口（默认是 8719）
 */
public class Demo {

    private static final Random random = new Random();
    private static final int COUNT = 20;

    public static void main(String[] args) {
        // 配置规则.
        initFlowRules();

        while (true) {
            // 1.5.0 版本开始可以直接利用 try-with-resources 特性
            try (Entry entry = SphU.entry("HelloWorld")) {
                // 被保护的逻辑
                System.out.println("hello world");
            } catch (BlockException ex) {
                // 处理被流控的逻辑
                System.out.println("blocked!");
            }

            int nextInt = random.nextInt((int) (1000 / COUNT * 1.5));
            try {
                Thread.sleep(nextInt);
            } catch (InterruptedException e) {
                System.exit(1);
            }
        }
    }


    @SentinelResource("HelloWorld")
    public void helloWorld() {
        // 资源中的逻辑
        System.out.println("hello world");
    }


    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("HelloWorld");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // Set limit QPS to 20.
        rule.setCount(COUNT);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
