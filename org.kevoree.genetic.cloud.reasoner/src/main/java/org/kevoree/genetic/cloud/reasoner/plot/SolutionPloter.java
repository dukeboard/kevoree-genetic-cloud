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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.kevoree.genetic.KevoreeEngineInstrument;
import org.kevoree.genetic.cloud.reasoner.SolutionFilter;
import org.kevoree.genetic.framework.KevoreeSolution;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        try {
            ChartUtilities.saveChartAsJPEG(new File("/Users/gregory.nain/Desktop/chart.jpeg"), chart, 1024, 500);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }


}
