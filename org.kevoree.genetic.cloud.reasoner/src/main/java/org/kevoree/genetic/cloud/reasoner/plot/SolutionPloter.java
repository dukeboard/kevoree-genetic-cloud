package org.kevoree.genetic.cloud.reasoner.plot;/*
* Author : Gregory Nain (developer.name@uni.lu)
* Date : 15/03/13
* (c) 2013 University of Luxembourg – Interdisciplinary Centre for Security Reliability and Trust (SnT)
* All rights reserved
*/

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.kevoree.genetic.KevoreeEngineInstrument;
import org.kevoree.genetic.cloud.reasoner.SolutionFilter;
import org.kevoree.genetic.framework.KevoreeSolution;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SolutionPloter implements KevoreeEngineInstrument {

    HashMap<String, LinkedList<Double>> timeResults = new HashMap<String, LinkedList<Double>>();


    public void processResult(List<KevoreeSolution> kevoreeSolutions) {
        if(!kevoreeSolutions.isEmpty()){
            SolutionFilter filter = new SolutionFilter();
            TreeSet<KevoreeSolution> orderedSolution = filter.order(kevoreeSolutions);
            KevoreeSolution bestSolution = orderedSolution.first();
            for(String fitness : bestSolution.getFitnessNames()) {
                LinkedList<Double> fitnessValues = timeResults.get(fitness);
                if(fitnessValues == null) {
                    fitnessValues = new LinkedList<Double>();
                    timeResults.put(fitness, fitnessValues);
                }
                fitnessValues.addLast(bestSolution.getResultFromFitness(fitness));
            }

            LinkedList<Double> fitnessValues = timeResults.get("mean");
            if(fitnessValues == null) {
                fitnessValues = new LinkedList<Double>();
                timeResults.put("mean", fitnessValues);
            }
            fitnessValues.addLast(bestSolution.getFitnessMean());

        }
    }


    public void plotResult(HashMap<String, LinkedList<Double>> timeResults) {
        this.timeResults = timeResults;
        plotResults();
    }

    public void plotResults() {

        XYSeriesCollection dataset = new XYSeriesCollection();

        for(String fitness : timeResults.keySet()) {
            XYSeries series = new XYSeries(fitness);
            LinkedList<Double> values = timeResults.get(fitness);
            int i = 0;
            for(Double val : values) {
                series.add(i++, val);
            }

            dataset.addSeries(series);
        }


// Create a simple XY chart

        // Add the series to your data set


        // Generate the graph
        JFreeChart chart = ChartFactory.createXYLineChart(
                "XY Chart",
                "x-axis",
                "y-axis",
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


}
