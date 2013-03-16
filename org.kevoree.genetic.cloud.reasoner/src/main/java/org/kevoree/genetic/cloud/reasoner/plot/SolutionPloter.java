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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SolutionPloter implements KevoreeEngineInstrument {

    HashMap<String, LinkedList<Measure>> timeResults = new HashMap<String, LinkedList<Measure>>();

    private long baseTimestamp = -1;
    public void processResult(List<KevoreeSolution> kevoreeSolutions) {
        if(baseTimestamp == -1) {
            baseTimestamp = System.currentTimeMillis();
        }
        long calltime = System.currentTimeMillis()-baseTimestamp;
        if(!kevoreeSolutions.isEmpty()){
            SolutionFilter filter = new SolutionFilter();
            TreeSet<KevoreeSolution> orderedSolution = filter.order(kevoreeSolutions);
            KevoreeSolution bestSolution = orderedSolution.first();
            for(String fitness : bestSolution.getFitnessNames()) {
                LinkedList<Measure> fitnessValues = timeResults.get(fitness);
                if(fitnessValues == null) {
                    fitnessValues = new LinkedList<Measure>();
                    timeResults.put(fitness, fitnessValues);
                }
                fitnessValues.addLast(new Measure(calltime, bestSolution.getResultFromFitness(fitness)));
            }

            LinkedList<Measure> fitnessValues = timeResults.get("mean");
            if(fitnessValues == null) {
                fitnessValues = new LinkedList<Measure>();
                timeResults.put("mean", fitnessValues);
            }
            fitnessValues.addLast(new Measure(calltime, bestSolution.getFitnessMean()));

        }
    }


    public void plotResult(HashMap<String, LinkedList<Measure>> timeResults) {
        this.timeResults = timeResults;
        plotResults();
    }

    public void plotResults() {
        plotXY();
        //plotTime();
        plotTimeInXY();
    }




    private void plotXY() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for(String fitness : timeResults.keySet()) {
            XYSeries series = new XYSeries(fitness);
            LinkedList<Measure> values = timeResults.get(fitness);
            int i = 0;
            for(Measure measure : values) {
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
            File temp = File.createTempFile("temp",".jpg");
            ChartUtilities.saveChartAsJPEG(temp, chart, 1500, 700);
            System.out.println("Plot => "+temp.getAbsolutePath());

            Desktop.getDesktop().open(temp);

        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }

    private void plotTimeInXY() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for(String fitness : timeResults.keySet()) {
            XYSeries series = new XYSeries(fitness);
            LinkedList<Measure> values = timeResults.get(fitness);
            for(Measure measure : values) {
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
            File temp = File.createTempFile("temp",".jpg");
            ChartUtilities.saveChartAsJPEG(temp, chart, 1500, 700);
            System.out.println("Plot => "+temp.getAbsolutePath());

            Desktop.getDesktop().open(temp);

        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }

    private void plotTime() {

        TimeSeriesCollection dataset = new TimeSeriesCollection();

        for(String fitness : timeResults.keySet()) {
            TimeSeries series = new TimeSeries(fitness,FixedMillisecond.class);
            LinkedList<Measure> values = timeResults.get(fitness);
            for(Measure m : values) {
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
            File temp = File.createTempFile("temp",".jpg");
            ChartUtilities.saveChartAsJPEG(temp, chart, 1500, 700);
            System.out.println("Plot => "+temp.getAbsolutePath());

            Desktop.getDesktop().open(temp);

        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }

}
