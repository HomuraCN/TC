package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class RandomContextWithOutput {

    // 1. 新增一个内部类，用于同时返回 文件名 和 找出的随机全0三元组坐标
    public static class Result {
        public String fileName;
        public int x;
        public int y;
        public int z;

        public Result(String fileName, int x, int y, int z) {
            this.fileName = fileName;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * 生成随机的三元形式背景
     */
    public static Result randomContext(int objSize, int ySize, int zSize, int m, String s) throws IOException {

        int attrSize = ySize * zSize;

        String fileName = "D:\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\random\\origin\\" + s + ".txt";
        Path path = Paths.get(fileName);

        try (BufferedWriter writer =
                     Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
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

        // ----------------- 新增逻辑：你的“随机找0”想法 -----------------
        int zeroX = -1, zeroY = -1, zeroZ = -1;
        // 只有当密度不为 100% 时才去找，否则会死循环
        if (m < 100) {
            while (true) {
                int rX = random.nextInt(objSize); // 随机生成 0 到 objSize-1
                int rJ = random.nextInt(attrSize); // 随机生成 0 到 attrSize-1

                // 如果对应位置是 0，我们就找到了！
                if (array[rX][rJ] == 0) {
                    zeroX = rX + 1; // 转换为 1-based 对象索引

                    // 将一维的列索引还原为 y 和 z (1-based)
                    zeroY = (rJ / zSize) + 1;
                    zeroZ = (rJ % zSize) + 1;
                    break;
                }
            }
        }
        // ---------------------------------------------------------------

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

        // 返回包装好的结果对象
        return new Result(fileName, zeroX, zeroY, zeroZ);
    }
}