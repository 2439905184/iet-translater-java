module _128hh_trans_jp2_zh {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.google.gson;
    
    //凡是使用@FXML的地方都要opens给javafx.fxml，一般就是controllers
    //用于声明该模块的指定包在runtime允许使用反射访问
    opens com.bigbrain._128hh_trans_jp2_zh.controller to javafx.fxml;
    opens com.bigbrain._128hh_trans_jp2_zh to  javafx.graphics;
    //主函数所在的包路径
    exports com.bigbrain._128hh_trans_jp2_zh.controller;
    
}