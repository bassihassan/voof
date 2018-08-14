package me.bassihassan;

import me.bassihassan.annotation.RowCell;
import me.bassihassan.annotation.RowIndex;
import org.apache.log4j.Logger;

public class Main {
    static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        Voof.fromFile("/home/post-6/Desktop/a.xlsx", Demo.class, (rowModel, errors) -> {
            logger.info("model => " + rowModel);
            logger.info("cell errors =>" + errors + " {}");
        });
    }

    static public class Demo {
        @RowIndex
        public Integer rowIndex;
        @RowCell(label = "a")
        public String a;
        @RowCell(label = "b")
        public Integer b;

        public Demo() {
        }


        @Override
        public String toString() {
            return "Demo{" +
                    "rowIndex=" + rowIndex +
                    ", a='" + a + '\'' +
                    ", b='" + b + '\'' +
                    '}';
        }
    }
}
