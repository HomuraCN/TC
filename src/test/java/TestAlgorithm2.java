import algorithm.DynamicTriadicUpdater;
import algorithm.Tradic;
import algorithm.TriadicConcept;
import org.junit.jupiter.api.Test;
import utils.Context;
import algorithm.File; // 引入你提供的 File 类
import utils.TriadicConceptGenerator;

import java.util.*;

public class TestAlgorithm2 {
    @Test
    void test(){
        // 1. 初始化原三元背景
        String filePath = "D:\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\context.txt";

        Tradic tradic = null;
        try {
            tradic = File.readFileToTradic(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 构建 Context
        Context context = new Context();
        context.setObjs_size(tradic.getX());
        context.setAttrs_size(tradic.getAttrsAndCondi_AttrSize());
        context.setAttrs(tradic.getAttrsAndCondi_Attr());
        context.setObjs(tradic.getAttrsAndCondi_Obj());

        // 2. 获取旧概念集合
        System.out.println("计算原背景的三元概念");
        List<TriadicConcept> oldConcepts = TriadicConceptGenerator.getAllTriadicConcepts(tradic, context);
        System.out.println("原背景三元概念总数: " + oldConcepts.size());

        // 3. 设定新增的三元组 (论文 例3 增加属性5 (1, 5, 1) )
        int newX = 1;
        int newZ = 1;
        System.out.println("\n新增三元组: 包含全新属性，位于对象 " + newX + ", 条件 " + newZ);

        long startTime = System.currentTimeMillis();

        // 4. 扩容底层矩阵数据结构 (生成 K+)
        Tradic newTradic = DynamicTriadicUpdater.expandTradicContext(tradic, newX, newZ);

        // 5. 定理4更新概念
        Set<TriadicConcept> finalConcepts = DynamicTriadicUpdater.generateByTheorem4(oldConcepts, tradic, newX, newZ);

        long endTime = System.currentTimeMillis();

        System.out.println("\n--- 第二类情况更新完成 ---");
        System.out.println("属性维度已从 " + tradic.getY() + " 扩充至 " + newTradic.getY());
        System.out.println("最新三元概念总数: " + finalConcepts.size());
        System.out.println("定理4更新耗时: " + (endTime - startTime) + " ms");

        // 结果输出
        for (TriadicConcept c : finalConcepts) {
            System.out.println(c);
        }
    }
}