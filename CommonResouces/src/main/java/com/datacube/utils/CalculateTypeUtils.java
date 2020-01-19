package com.datacube.utils;

/**
 * @author Dale
 * @create 2019-12-20 16:11
 */
public class CalculateTypeUtils {


    public static String calculateType(String key,String value){

        String sql = "";
        switch (value){
            case "SUM":
                sql = "sum("+key+") as sum_"+key+",";break;
            case "MEAN":
                sql = "avg("+key+") as mean_"+key+",";break;
            case "MAX":
                sql = "max("+key+") as max_"+key+",";break;
            case "MIN":
                sql = "min("+key+") as min_"+key+",";break;
            case "Count":
                sql = "count("+key+") as count_"+key+",";break;
            case "CountD":
                sql = "count(distinct("+key+")) as countd_"+key+",";break;
            default:
                break;
        }
        return sql;
    }

    public static String resultType(String key,String value){
        //返回要求采用sun(Age)这种形式,但计算时列名往往会变化,统一用as取别名
        //由于SparkSql中as取别名时带()会无法识别报错,所以采用下划线的方式,计算后在装换回来
        String nameAndCalculate = "";
        switch (value){
            case "SUM":
                nameAndCalculate = "sum_"+key;break;
            case "MEAN":
                nameAndCalculate = "mean_"+key;break;
            case "MAX":
                nameAndCalculate = "max_"+key;break;
            case "MIN":
                nameAndCalculate = "min_"+key;break;
            case "Count":
                nameAndCalculate = "count_"+key;break;
            case "CountD":
                nameAndCalculate = "countd_"+key;break;
            default:
                break;
        }
        return nameAndCalculate;
    }
    //根据数据类型的string装换为特定的基本类型
    public static Object dataType(String type, String value){

        switch (type){
            case "class java.lang.Short":
                return Short.parseShort(value);
            case "class java.lang.Integer":
                return Integer.parseInt(value);
            case "class java.lang.Long":
                return Long.parseLong(value);
            case "class java.lang.Double":
                return Double.parseDouble(value);
            case "class java.lang.Float":
                return Float.parseFloat(value);
            case "class java.lang.Byte":
                return Byte.parseByte(value);
            default:
                return value;
        }

    }

}
/*
select count(Age),sum(Parch) from featureTab group by Embarked,Pclass-STRING,Sex;
计算方式分为13种:
        //总数 SUM
       //平均数 MEAN
        中位数 MEDIAN
        众数 MODE
       //最大值 MAX
        //最小值 MIN
        分位数 legend: "PERCENTILE", probability: 0.5
        样本方差 VAR
        总体方差	VARP
        样本标准差 STDEV
        总体标准差 STDEVP
        //数量 Count
        //维数(去重) CountD*/