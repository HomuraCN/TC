package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class RandomContext {
    /**
     * 生成随机的三元形式背景
     * @param objSize 对象数量 (X)
     * @param ySize 属性数量 (Y)
     * @param zSize 条件数量 (Z)
     * @param m 密度百分比 (例如30代表30%)
     * @param s 文件名
     */
    public static String randomContext(int objSize, int ySize, int zSize, int m, String s) throws IOException {

        // 三元背景展平后的总列数 = 属性数量 * 条件数量
        int attrSize = ySize * zSize;

        String fileName = "D:\\Homura\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\random\\origin\\" + s + ".txt";
        Path path = Paths.get(fileName);

        try (BufferedWriter writer =
                     Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            // 将文件头修改为输出三个维度的信息：对象数 属性数 条件数
            writer.write(objSize + " " + ySize + " " + zSize + "\r\n");
        }

        int[][] array = new int[objSize][attrSize];
        Random random = new Random();
        random.setSeed(System.nanoTime());
        int count = objSize;

        // 保证每行至少有一个1
        for (int i = 0; i < objSize; i++) {
            int j = random.nextInt(attrSize);
            array[i][j] = 1;
        }

        // 保证每列至少有一个1
        for (int j = 0; j < attrSize; j++) {
            int i = random.nextInt(objSize);
            if (array[i][j] == 0) {
                array[i][j] = 1;
                count++;
            }
        }

        // 根据密度 m 填充剩余的 1
        int onesCount =  objSize * attrSize * m / 100 - count;
        System.out.println("还需要随机填充的 1 的个数: " + onesCount);
        while (onesCount > 0) {
            int i = random.nextInt(objSize);
            int j = random.nextInt(attrSize);
            if (array[i][j] == 0) {
                array[i][j] = 1;
                onesCount--;
            }
        }

        // 写入矩阵数据
        try (BufferedWriter writer =
                     Files.newBufferedWriter(path,
                             StandardCharsets.UTF_8,
                             StandardOpenOption.APPEND)) {
            for (int i = 0; i < objSize; i++) {
                for (int j = 0; j < attrSize; j++) {
                    writer.write(array[i][j] + " ");
                }
                writer.write("\r\n");
            }
        }

        return fileName;
    }
}