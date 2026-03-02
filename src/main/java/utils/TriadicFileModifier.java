package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TriadicFileModifier {

    /**
     * 针对算法2（新增全新属性），在原背景文件中，为每个条件块追加一列新属性。
     * @param extent   新增关系的对象索引 (从 1 开始)
     * @param modus    新增关系的条件索引 (从 1 开始)
     * @param filePath 原始的三元背景文件路径
     * @param filePathUpdate 更新的三元背景文件路径
     * @return 生成的新文件路径
     */
    public static String createUpdatedFileAlgo2(int extent, int modus, String filePath, String filePathUpdate) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        // 解析第一行维度信息
        String[] dims = lines.get(0).trim().split("\\s+");
        int objSize = Integer.parseInt(dims[0]);
        int ySize = Integer.parseInt(dims[1]);
        int zSize = Integer.parseInt(dims[2]);

        // 新的属性数量
        int y_new = ySize + 1;

        String newFilePath = filePathUpdate.replace(".txt", "UpdateAlgo2.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(newFilePath))) {
            // 1. 写入新的头部
            writer.write(objSize + " " + y_new + " " + zSize + "\n");

            // 2. 遍历每一个对象的数据行
            for (int i = 1; i <= objSize; i++) {
                String[] originalVals = lines.get(i).trim().split("\\s+");
                List<String> newVals = new ArrayList<>();

                // 按条件 (modus) 进行分块遍历，总共有 zSize 个块
                for (int k = 1; k <= zSize; k++) {

                    // a) 先把这个条件块原来的 ySize 个属性值抄下来
                    int startIndex = (k - 1) * ySize;
                    for (int j = 0; j < ySize; j++) {
                        newVals.add(originalVals[startIndex + j]);
                    }

                    // b) 在这个条件块的末尾，追加第 y_new 个新属性的值
                    // 只有当当前行是目标对象 (extent)，且当前块是目标条件 (modus) 时，才填 1
                    if (i == extent && k == modus) {
                        newVals.add("1");
                    } else {
                        newVals.add("0");
                    }
                }

                // 写入新文件
                writer.write(String.join(" ", newVals) + "\n");
            }
        }
        return newFilePath;
    }
}