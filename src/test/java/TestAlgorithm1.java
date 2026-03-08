import algorithm.*;
import org.junit.jupiter.api.Test;
import utils.Context;
import utils.TriadicConceptGenerator;
import utils.TriadicConceptGenerator1;
import utils.TriadicConceptGenerator2;

import java.util.*;

public class TestAlgorithm1 {
    @Test
    void test(){
        // 1. 初始化原三元背景
        String filePath = "D:\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\context.txt"; // 请替换为实际包含例1数据的文件路径

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

        // 3. 设定新增的三元组 (论文 例2 新增 (3, 4, 1))
        int newX = 2;
        int newY = 2;
        int newZ = 1;
        System.out.println("\n新增三元组: (" + newX + ", " + newY + ", " + newZ + ")");

        long startTime = System.currentTimeMillis();
        Set<TriadicConcept> finalConcepts = new HashSet<>();

        // 4. 应用定理1：保留未被破坏的旧概念
        for (TriadicConcept oldC : oldConcepts) {
            if (DynamicTriadicUpdater.isPreservedByTheorem1(oldC, tradic, newX, newY, newZ)) {
                finalConcepts.add(oldC);
            }
        }
        System.out.println("定理1保留的旧概念数量: " + finalConcepts.size());

        // 5. 更新底层 Tradic 数据结构以应用定理2
        DynamicTriadicUpdater.addRelationToTradic(tradic, newX, newY, newZ);
        Context newContext = new Context();
        newContext.setAttrs(tradic.getAttrsAndCondi_Attr());
        newContext.setObjs(tradic.getAttrsAndCondi_Obj());
        newContext.setObjs_size(tradic.getX());
        newContext.setAttrs_size(tradic.getAttrsAndCondi_AttrSize());

        // 6. 应用定理2：生成局部受影响的新概念
        Set<TriadicConcept> newGenConcepts = DynamicTriadicUpdater.generateByTheorem2(tradic, newContext, newX, newY, newZ);
        System.out.println("定理2新生成/更新的概念数量: " + newGenConcepts.size());

        finalConcepts.addAll(newGenConcepts);
        long endTime = System.currentTimeMillis();

        System.out.println("\n--- 第一类情况更新完成 ---");
        System.out.println("最新三元概念总数: " + finalConcepts.size());
        System.out.println("增量更新耗时: " + (endTime - startTime) + " ms");

        // 结果输出
        for (TriadicConcept c : finalConcepts) {
            System.out.println(c);
        }
    }

    @Test
    void test2_2(){
        String filePath = "D:\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\random\\origin\\randomContext.txt";

        Tradic tradic = null;
        try {
            tradic = File2.readFileToTradic(filePath); // 调用 File2
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Context context = new Context();
        context.setObjs_size(tradic.getX());
        context.setAttrs_size(tradic.getAttrsAndCondi_AttrSize());
        context.setAttrs(tradic.getAttrsAndCondi_Attr());
        context.setObjs(tradic.getAttrsAndCondi_Obj());

        System.out.println("计算原背景的三元概念");
        List<TriadicConcept> oldConcepts = TriadicConceptGenerator2.getAllTriadicConcepts(tradic, context);
        System.out.println("原背景三元概念总数: " + oldConcepts.size());

        int newX = 5; int newY = 7; int newZ = 24;
        System.out.println("\n[定理 2_2] 新增三元组: (" + newX + ", " + newY + ", " + newZ + ")");

        long startTime = System.currentTimeMillis();
        Set<TriadicConcept> finalConcepts = new HashSet<>();

        for (TriadicConcept oldC : oldConcepts) {
            if (DynamicTriadicUpdater.isPreservedByTheorem1(oldC, tradic, newX, newY, newZ)) {
                finalConcepts.add(oldC);
            }
        }

        DynamicTriadicUpdater.addRelationToTradic(tradic, newX, newY, newZ);

        Context contextK2 = new Context();
        contextK2.setObjs_size(tradic.getY());
        contextK2.setAttrs_size(tradic.getZ() * tradic.getX());
        contextK2.setAttrs(tradic.getCondiAndobjs_Attr());
        contextK2.setObjs(tradic.getCondiAndobjs_Obj());

        Set<TriadicConcept> newGenConcepts = DynamicTriadicUpdater.generateByTheorem2_2(tradic, contextK2, newX, newY, newZ);
        finalConcepts.addAll(newGenConcepts);

        System.out.println("最新三元概念总数: " + finalConcepts.size());
        System.out.println("增量更新耗时: " + (System.currentTimeMillis() - startTime) + " ms");

        for (TriadicConcept c : finalConcepts) {
            System.out.println(c);
        }
    }

    @Test
    void test2_3(){
        String filePath = "D:\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\context.txt";

        Tradic tradic = null;
        try {
            tradic = File2.readFileToTradic(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Context context = new Context();
        context.setObjs_size(tradic.getX());
        context.setAttrs_size(tradic.getAttrsAndCondi_AttrSize());
        context.setAttrs(tradic.getAttrsAndCondi_Attr());
        context.setObjs(tradic.getAttrsAndCondi_Obj());

        System.out.println("计算原背景的三元概念");
        List<TriadicConcept> oldConcepts = TriadicConceptGenerator1.getAllTriadicConcepts(tradic, context);

        int newX = 1; int newY = 2; int newZ = 3;
        System.out.println("\n[定理 2_3] 新增三元组: (" + newX + ", " + newY + ", " + newZ + ")");

        long startTime = System.currentTimeMillis();
        Set<TriadicConcept> finalConcepts = new HashSet<>();

        for (TriadicConcept oldC : oldConcepts) {
            if (DynamicTriadicUpdater.isPreservedByTheorem1(oldC, tradic, newX, newY, newZ)) {
                finalConcepts.add(oldC);
            }
        }

        DynamicTriadicUpdater.addRelationToTradic(tradic, newX, newY, newZ);

        Context contextK3 = new Context();
        contextK3.setObjs_size(tradic.getZ());
        contextK3.setAttrs_size(tradic.getX() * tradic.getY());
        contextK3.setAttrs(tradic.getObjsAndAttrs_Attr());
        contextK3.setObjs(tradic.getObjsAndAttrs_Obj());

        Set<TriadicConcept> newGenConcepts = DynamicTriadicUpdater.generateByTheorem2_3(tradic, contextK3, newX, newY, newZ);
        finalConcepts.addAll(newGenConcepts);

        System.out.println("最新三元概念总数: " + finalConcepts.size());
        System.out.println("增量更新耗时: " + (System.currentTimeMillis() - startTime) + " ms");

        for (TriadicConcept c : finalConcepts) {
            System.out.println(c);
        }
    }
}