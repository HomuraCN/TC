import algorithm.DynamicTriadicUpdater;
import algorithm.Tradic;
import algorithm.TriadicConcept;
import utils.Context;
import algorithm.File; // 引入你提供的 File 类
import utils.TriadicConceptGenerator;

import java.util.*;

public class TestCase1 {
    public static void main(String[] args) throws Exception { // 添加 throws Exception
        // 1. 初始化原三元背景
        String filePath = "D:\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\context.txt"; // 请替换为实际包含例1数据的文件路径

        // 正确调用 File 类
        Tradic tradic = File.readFileToTradic(filePath);

        // 手动从 tradic 构建诱导形式背景 Context
        Context context = new Context();
        context.setObjs_size(tradic.getX());
        context.setAttrs_size(tradic.getAttrsAndCondi_AttrSize());
        context.setAttrs(tradic.getAttrsAndCondi_Attr());
        context.setObjs(tradic.getAttrsAndCondi_Obj());

        // 2. 获取旧概念集合
        System.out.println("正在计算原背景的三元概念...");
        List<TriadicConcept> oldConcepts = TriadicConceptGenerator.getAllTriadicConcepts(tradic, context);
        System.out.println("原背景三元概念总数: " + oldConcepts.size());

        // 3. 设定新增的三元组 (对应论文 例2 中新增的 (3, 4, 1))
        int newX = 3;
        int newY = 4;
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

        // 打印结果比对论文例2
        for (TriadicConcept c : finalConcepts) {
            System.out.println(c);
        }
    }
}