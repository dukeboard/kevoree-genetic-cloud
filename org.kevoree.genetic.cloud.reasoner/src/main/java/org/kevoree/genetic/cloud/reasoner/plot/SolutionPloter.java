package org.kevoree.genetic.cloud.reasoner.plot;/*
* Author : Gregory Nain (developer.name@uni.lu)
* Date : 15/03/13
* (c) 2013 University of Luxembourg – Interdisciplinary Centre for Security Reliability and Trust (SnT)
* All rights reserved
*/

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.kevoree.genetic.KevoreeEngineInstrument;
import org.kevoree.genetic.cloud.reasoner.SolutionFilter;
import org.kevoree.genetic.cloud.reasoner.util.Measure;
import org.kevoree.genetic.framework.KevoreeSolution;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SolutionPloter implements KevoreeEngineInstrument {

    HashMap<String, LinkedList<Measure>> timeResults = new HashMap<String, LinkedList<Measure>>();

    private long baseTimestamp = -1;

    public void processResult(List<KevoreeSolution> kevoreeSolutions) {
        if(baseTimestamp == -1){
            baseTimestamp = System.currentTimeMillis();
        }

        long calltime = System.currentTimeMillis() - baseTimestamp;
        if (!kevoreeSolutions.isEmpty()) {
            SolutionFilter filter = new SolutionFilter();
            TreeSet<KevoreeSolution> orderedSolution = filter.order(kevoreeSolutions);
            KevoreeSolution bestSolution = orderedSolution.first();
            for (String fitness : bestSolution.getFitnessNames()) {
                LinkedList<Measure> fitnessValues = timeResults.get(fitness);
                if (fitnessValues == null) {
                    fitnessValues = new LinkedList<Measure>();
                    timeResults.put(fitness, fitnessValues);
                }
                fitnessValues.addLast(new Measure(calltime, bestSolution.getResultFromFitness(fitness)));
            }
            if(bestSolution.getFitnessNames().size() > 1){
                LinkedList<Measure> fitnessValues = timeResults.get("mean");
                if (fitnessValues == null) {
                    fitnessValues = new LinkedList<Measure>();
                    timeResults.put("mean", fitnessValues);
                }
                fitnessValues.addLast(new Measure(calltime, bestSolution.getFitnessMean()));
            }


        }
    }


    public void plotResult(HashMap<String, LinkedList<Measure>> timeResults) {
        this.timeResults = timeResults;
        plotResults();
    }

    public void plotResults() {
        //plotXY();
        //plotTime();
        //plotTimeInXY();
        plotR();
    }


    private String prefixe = "empty";

    public String getPrefixe() {
        return prefixe;
    }

    public void setPrefixe(String prefixe) {
        this.prefixe = prefixe;
    }


    private void plotR() {
        StringBuffer Rbuffer = new StringBuffer();
        List<String> colors = Arrays.asList("green", "blue", "black", "gray", "yellow");


        int fitNb = 0;
        for (String fitness : timeResults.keySet()) {

            StringBuffer temp = new StringBuffer();
            StringBuffer temp_time = new StringBuffer();
            temp.append(prefixe + "_" + fitness + " <- c(");
            temp_time.append(prefixe + "_" + fitness + "_t <- c(");
            LinkedList<Measure> values = timeResults.get(fitness);
            int i = 0;
            for (Measure measure : values) {
                if (i != 0) {
                    temp.append(",");
                    temp_time.append(",");
                }
                temp.append(measure.value);
                temp_time.append(measure.timestamp);
                i++;
            }
            temp.append(")\n");
            temp_time.append(")\n");

            Rbuffer.append(temp.toString());
            Rbuffer.append(temp_time.toString());

            Rbuffer.append("par(new = TRUE)\n");

            String color = "";
            String size = "2";
            if (fitness.equals("mean")) {
                color = "\"red\"";
                size = "4";
                System.out.println("MeanDetected");
            } else {
                color = "topo.colors(" + timeResults.keySet().size() + ")[" + (fitNb + 1) + "]";
            }
            String plot = "lines(";
            if (fitNb == 0) {
                plot = "plot(xlab=\"Time (ms)\",ylab=\"Fitness Score (%)\",ylim=c(0,100),";
            }

            Rbuffer.append(plot + "" + prefixe + "_" + fitness + "_t, " + prefixe + "_" + fitness + ",type=\"l\",col=" + color + ",lwd=" + size + ",main=NULL)\n");
            fitNb++;
        }

        Rbuffer.append("legend(2000,100,c(");
        fitNb = 0;
        for (String fitness : timeResults.keySet()) {
            if (fitNb != 0) {
                Rbuffer.append(",");
            }
            Rbuffer.append("\""+fitness+"\"");
            fitNb++;
        }
        fitNb = 0;
        Rbuffer.append("),col=c(");
        for (String fitness : timeResults.keySet()) {
            String color = "";
            if (fitness.equals("mean")) {
                color = "\"red\"";
            } else {
                color = "topo.colors(" + timeResults.keySet().size() + ")[" + (fitNb + 1) + "]";
            }
            if (fitNb != 0) {
                Rbuffer.append(",");
            }
            Rbuffer.append(color);
            fitNb++;
        }
        fitNb = 0;
        Rbuffer.append("),lty=c(");
        for (String fitness : timeResults.keySet()) {
            if (fitNb != 0) {
                Rbuffer.append(",");
            }
            Rbuffer.append("1");
            fitNb++;
        }
        fitNb = 0;
        Rbuffer.append("),lwd=c(");
        for (String fitness : timeResults.keySet()) {
            if (fitNb != 0) {
                Rbuffer.append(",");
            }
            if(fitness.equals("mean")){
                Rbuffer.append("3");

            } else {
                Rbuffer.append("2");
            }
            fitNb++;
        }
        Rbuffer.append(")");
        Rbuffer.append(")");

        try {
            File tempRoutput = File.createTempFile("temp", ".r");
            FileWriter writer = new FileWriter(tempRoutput);
            writer.write(Rbuffer.toString());
            writer.flush();
            writer.close();
            System.out.println("RData => " + tempRoutput.getAbsolutePath());
            Desktop.getDesktop().open(tempRoutput);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }

    }

    private void plotXY() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (String fitness : timeResults.keySet()) {
            XYSeries series = new XYSeries(fitness);
            LinkedList<Measure> values = timeResults.get(fitness);
            int i = 0;
            for (Measure measure : values) {
                series.add(i++, measure.value);
            }
            dataset.addSeries(series);
        }

        // Generate the graph
        JFreeChart chart = ChartFactory.createXYLineChart(
                "By generation",
                "Generation index",
                "SLA satisfaction",
                dataset,
                PlotOrientation.VERTICAL,  // Plot Orientation
                true,                      // Show Legend
                true,                      // Use tooltips
                false                      // Configure chart to generate URLs?
        );

        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(0, Color.black);
        renderer.setSeriesPaint(1, Color.blue);
        renderer.setSeriesPaint(2, Color.red);
        renderer.setSeriesPaint(3, Color.gray);


        try {
            File temp = File.createTempFile("temp", ".jpg");
            ChartUtilities.saveChartAsJPEG(temp, chart, 1500, 700);
            System.out.println("Plot => " + temp.getAbsolutePath());
            Desktop.getDesktop().open(temp);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }

    private void plotTimeInXY() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (String fitness : timeResults.keySet()) {
            XYSeries series = new XYSeries(fitness);
            LinkedList<Measure> values = timeResults.get(fitness);
            for (Measure measure : values) {
                System.out.println("" + measure.timestamp + ":" + measure.value);
                series.add(measure.timestamp, measure.value);
            }
            dataset.addSeries(series);
        }


        // Generate the graph
        JFreeChart chart = ChartFactory.createXYLineChart(
                "TimeChart",
                "Time (in ms)",
                "SLA satisfaction",
                dataset,
                PlotOrientation.VERTICAL,  // Plot Orientation
                true,                      // Show Legend
                true,                      // Use tooltips
                false                      // Configure chart to generate URLs?
        );

        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(0, Color.black);
        renderer.setSeriesPaint(1, Color.blue);
        renderer.setSeriesPaint(2, Color.red);
        renderer.setSeriesPaint(3, Color.gray);


        try {
            File temp = File.createTempFile("temp", ".jpg");
            ChartUtilities.saveChartAsJPEG(temp, chart, 1500, 700);
            System.out.println("Plot => " + temp.getAbsolutePath());

            Desktop.getDesktop().open(temp);

        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }

    private void plotTime() {

        TimeSeriesCollection dataset = new TimeSeriesCollection();

        for (String fitness : timeResults.keySet()) {
            TimeSeries series = new TimeSeries(fitness, FixedMillisecond.class);
            LinkedList<Measure> values = timeResults.get(fitness);
            for (Measure m : values) {
                series.add(new FixedMillisecond(m.timestamp), m.value);
            }
            dataset.addSeries(series);
        }


        // Generate the graph
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "TimeChart",
                "Time (min:sec:msec)",
                "SLA satisfaction",
                dataset,
                true,                      // Show Legend
                true,                      // Use tooltips
                false                      // Configure chart to generate URLs?
        );

        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(0, Color.black);
        renderer.setSeriesPaint(1, Color.blue);
        renderer.setSeriesPaint(2, Color.red);
        renderer.setSeriesPaint(3, Color.gray);

        DateAxis axis = (DateAxis) chart.getXYPlot().getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("mm:ss:S"));

        try {
            File temp = File.createTempFile("temp", ".jpg");
            ChartUtilities.saveChartAsJPEG(temp, chart, 1500, 700);
            System.out.println("Plot => " + temp.getAbsolutePath());

            Desktop.getDesktop().open(temp);

        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }

}
