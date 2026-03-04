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

    // 1. 新增 updatedFileName 字段，用于同时返回两个文件名和坐标
    public static class Result {
        public String fileName;
        public String updatedFileName; // 新增：更新后的文件名
        public int x;
        public int y;
        public int z;

        public Result(String fileName, String updatedFileName, int x, int y, int z) {
            this.fileName = fileName;
            this.updatedFileName = updatedFileName;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * 生成随机的三元形式背景
     * 增加了 s_updated 参数，代表把 0 变成 1 后的新文件名
     */
    public static Result randomContext(int objSize, int ySize, int zSize, int m, String s, String s_updated) throws IOException {

        int attrSize = ySize * zSize;

        String fileName = "D:\\Homura\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\random\\origin\\" + s + ".txt";
        String updatedFileName = "D:\\Homura\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\random\\update\\" + s_updated + ".txt";

        Path path = Paths.get(fileName);
        Path updatedPath = Paths.get(updatedFileName);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(objSize + " " + ySize + " " + zSize + "\r\n");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(updatedPath, StandardCharsets.UTF_8)) {
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

        // ----------------- 修正后的“随机找0”逻辑 -----------------
        int zeroX = -1, zeroY = -1, zeroZ = -1;
        int foundRx = -1, foundRj = -1; // 记录找到的 0 在二维数组中的原始坐标

        if (m < 100) {
            while (true) {
                int rX = random.nextInt(objSize); // 0 到 objSize-1
                int rJ = random.nextInt(attrSize); // 0 到 attrSize-1

                if (array[rX][rJ] == 0) {
                    zeroX = rX + 1; // 对象 x

                    // 正确的逆向映射：矩阵是按条件(z)分块的，每个块大小为 ySize
                    zeroZ = (rJ / ySize) + 1; // 除以 ySize 的商，代表它落在了第几个条件块
                    zeroY = (rJ % ySize) + 1; // 除以 ySize 的余数，代表它是该块里的第几个属性

                    foundRx = rX;
                    foundRj = rJ;

                    break;
                }
            }
        }
        // ---------------------------------------------------------------

        // 1. 将原矩阵写入原文件 (此时 array 里还是 0)
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            for (int i = 0; i < objSize; i++) {
                for (int j = 0; j < attrSize; j++) {
                    writer.write(array[i][j] + " ");
                }
                writer.write("\r\n");
            }
        }

        // 2. 将随机找到的那个 0 翻转为 1
        if (foundRx != -1 && foundRj != -1) {
            array[foundRx][foundRj] = 1;
        }

        // 3. 将翻转后的新矩阵写入新文件
        try (BufferedWriter writer = Files.newBufferedWriter(updatedPath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            for (int i = 0; i < objSize; i++) {
                for (int j = 0; j < attrSize; j++) {
                    writer.write(array[i][j] + " ");
                }
                writer.write("\r\n");
            }
        }

        // 返回包装好的结果对象，包含了原文件、新文件以及变更的坐标
        return new Result(fileName, updatedFileName, zeroX, zeroY, zeroZ);
    }
}