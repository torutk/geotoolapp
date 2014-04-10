/*
 * The MIT License
 *
 * Copyright 2012 Toru Takahashi <torutk@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package shpviewer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiLineString;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;

/**
 * 指定したESRI shape形式の地図データファイルを読み込み、地図データを可視化する。
 *
 * GISライブラリにGeoTools 8.xを利用している。
 */
public class ShapefileViewer {

    /**
     * プログラムのエントリメソッド。
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if (file == null) {
            return;
        }
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = store.getFeatureSource();

        MapContent map = new MapContent();
        map.setTitle("簡素なシェープファイルビューア");

        // 表示スタイルを作成しレイヤを生成、MapContentのレイヤに追加する
        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer layer = new FeatureLayer(featureSource, style);
        map.addLayer(layer);

        JMapFrame.showMap(map);

        printFeatureSource(featureSource);
    }

    private static void printFeatureSource(SimpleFeatureSource featureSource) throws IOException {
        SimpleFeatureCollection features = featureSource.getFeatures();
        try (SimpleFeatureIterator iter = features.features()) {
            boolean isFeaturePrinted = false;
            while (iter.hasNext()) {
                SimpleFeature feature = iter.next();
                //feature.getAttributes().stream().forEach(System.out::println);
                //System.out.println(feature.getName());
                //System.out.println(feature.getDefaultGeometry().getClass().getName());
                if (!isFeaturePrinted) {
                    printFeature(feature);
                    isFeaturePrinted = true;
                }
            }
        }
    }

    private static void printFeature(SimpleFeature feature) {
        System.out.println("====== Feature Details ======");
        System.out.println("class name of feature is " + feature.getClass().getName());
        Object geom = feature.getDefaultGeometry();
        if (geom instanceof MultiLineString) {
            MultiLineString mls = (MultiLineString) geom;
            Coordinate[] vertices = mls.getCoordinates();
            System.out.println("number of vertex is " + vertices.length);
            System.out.println("1st vertex, x = " + vertices[0].x + ", y = " + vertices[0].y);
        }

    }

}
