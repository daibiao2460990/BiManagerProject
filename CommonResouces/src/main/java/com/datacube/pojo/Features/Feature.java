package com.datacube.pojo.Features;
import java.util.List;

/**
 * Auto-generated: 2019-12-10 15:43:16
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Feature {

    private String name;
    private String legend;
    private Color color;
    private List<Labels> labels;
    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setLegend(String legend) {
         this.legend = legend;
     }
     public String getLegend() {
         return legend;
     }

    public void setColor(Color color) {
         this.color = color;
     }
     public Color getColor() {
         return color;
     }

    public void setLabels(List<Labels> labels) {
         this.labels = labels;
     }
     public List<Labels> getLabels() {
         return labels;
     }

}