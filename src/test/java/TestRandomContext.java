import org.junit.jupiter.api.Test;
import utils.RandomContext;

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
}