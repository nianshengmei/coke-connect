package org.needcoke.c;

import cn.hutool.core.util.ReUtil;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
public class CApplication {

    public static void main(String[] args) {

        /* by warren: 横向模糊匹配 */
        // String regex = "ab{2,3}c"; String content = "abbbbc";‘

        /* by warren: 纵向匹配 */
        String regex = "a[123]b";String content = "a1b";
        //String regex = ""; String content = "";
        boolean match = ReUtil.isMatch(regex, content);
        System.out.println(match);
    }
}
