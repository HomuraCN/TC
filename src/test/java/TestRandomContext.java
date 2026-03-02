import org.junit.jupiter.api.Test;
import utils.RandomContext;
import utils.RandomContextWithOutput;
import utils.TriadicFileModifier;

import java.io.IOException;

public class TestRandomContext {
    @Test
    void test(){
        try {
            // 参数依次为: 对象数(3), 属性数(4), 条件数(3), 密度(30%), 文件名("randomContext")
            System.out.println(RandomContext.randomContext(20, 10, 4, 30, "randomContext"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void test1(){
        try {
            RandomContextWithOutput.Result res = RandomContextWithOutput.randomContext(20, 10, 4, 30, "randomContext");
            System.out.println("生成的文件路径: " + res.fileName);
            System.out.println("随机选取的 0 坐标为: (x=" + res.x + ", y=" + res.y + ", z=" + res.z + ")");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void test2(){
        try {
            System.out.println(TriadicFileModifier.createUpdatedFileAlgo2(3, 1,
                    "D:\\Homura\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\random\\origin\\randomContext.txt",
                    "D:\\Homura\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\random\\update\\randomContext.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}