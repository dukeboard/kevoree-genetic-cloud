package org.kevoree.genetic.cloud.reasoner.plot;

import org.kevoree.genetic.KevoreeEngineInstrument;
import org.kevoree.genetic.cloud.reasoner.SolutionFilter;
import org.kevoree.genetic.cloud.reasoner.util.Measure;
import org.kevoree.genetic.framework.KevoreeSolution;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SolutionPloter implements KevoreeEngineInstrument {

    public enum PointType {point, line;}

    public HashMap<String, PointType> pointConfig = new HashMap<String, PointType>();

    private Integer nbSolutionToPrint = 1;

    public SolutionPloter setBestSolutionNumber(Integer solNB) {
        nbSolutionToPrint = solNB;
        return this;
    }


    HashMap<String, LinkedList<Measure>> timeResults = new HashMap<String, LinkedList<Measure>>();

    private long baseTimestamp = -1;

    public void processResult(List<KevoreeSolution> kevoreeSolutions) {
        if (baseTimestamp == -1) {
            baseTimestamp = System.currentTimeMillis();
        }

        long calltime = System.currentTimeMillis() - baseTimestamp;
        if (!kevoreeSolutions.isEmpty()) {
            SolutionFilter filter = new SolutionFilter();
            TreeSet<KevoreeSolution> orderedSolution = filter.order(kevoreeSolutions);
            for (int i = 0; i < nbSolutionToPrint; i++) {
                KevoreeSolution bestSolution = orderedSolution.pollFirst();
                if (nbSolutionToPrint == 1) {
                    for (String fitness : bestSolution.getFitnessNames()) {
                        LinkedList<Measure> fitnessValues = timeResults.get(fitness);
                        if (fitnessValues == null) {
                            fitnessValues = new LinkedList<Measure>();
                            timeResults.put(fitness, fitnessValues);
                        }
                        fitnessValues.addLast(new Measure(calltime, bestSolution.getResultFromFitness(fitness)));
                    }
                }

                if (bestSolution != null && bestSolution.getFitnessNames().size() > 1) {
                    LinkedList<Measure> fitnessValues = timeResults.get("mean");
                    if (fitnessValues == null) {
                        fitnessValues = new LinkedList<Measure>();
                        timeResults.put("mean", fitnessValues);

                        if (nbSolutionToPrint != 1) {
                            pointConfig.put("mean", PointType.point);
                        }

                    }
                    fitnessValues.addLast(new Measure(calltime, bestSolution.getFitnessMean()));
                }
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
        try {
            plotR();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private String prefixe = "empty";

    public String getPrefixe() {
        return prefixe;
    }

    public void setPrefixe(String prefixe) {
        this.prefixe = prefixe;
    }


    private void plotR() throws InterruptedException {
        StringBuffer Rbuffer = new StringBuffer();
        List<String> colors = Arrays.asList("black", "blue", "gray", "yellow", "green");


        int fitNb = 0;
        for (String fitness : timeResults.keySet()) {

            StringBuffer temp = new StringBuffer();
            StringBuffer temp_time = new StringBuffer();
            temp.append(prefixe + "_" + fitness.replace("/", "_") + " <- c(");
            temp_time.append(prefixe + "_" + fitness.replace("/", "_") + "_t <- c(");
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
            String size = "1.8";
            if (fitness.equals("mean")) {
                color = "\"red\"";
                size = ".8";
            } else {
                color = "topo.colors(" + timeResults.keySet().size() + ")[" + (fitNb + 1) + "]";
            }
            String plot = "lines(";
            if (fitNb == 0) {
                plot = "plot(xlab=\"Time (ms)\",ylab=\"Fitness Score (%)\",ylim=c(0,100),";
            }

            String pointType = "l";
            String charType = "";
            if (pointConfig.containsKey(fitness)) {
                if (pointConfig.get(fitness).equals(PointType.line)) {
                    pointType = "l";
                }
                if (pointConfig.get(fitness).equals(PointType.point)) {
                    pointType = "p";
                    charType = ",pch=20, cex = .5";
                }
            }


            Rbuffer.append(plot + "" + prefixe + "_" + fitness.replace("/", "_") + "_t, " + prefixe + "_" + fitness.replace("/", "_") + ",type=\"" + pointType + "\",col=" + color + ",lwd=" + size + ",main=NULL" + charType + ")\n");
            fitNb++;
        }

        Rbuffer.append("legend(2000,100,c(");
        fitNb = 0;
        for (String fitness : timeResults.keySet()) {
            if (fitNb != 0) {
                Rbuffer.append(",");
            }
            Rbuffer.append("\"" + fitness.replace("/", "_") + "\"");
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
            if (fitness.equals("mean")) {
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


            File tempRoutputPdf = File.createTempFile("temp", ".pdf");

            Rbuffer.append("\n");
            Rbuffer.insert(0, "pdf(\"" + tempRoutputPdf.getAbsolutePath() + "\",width=6,height=4)\n");

            Rbuffer.append("dev.off()\n");

            FileWriter writer = new FileWriter(tempRoutput);
            writer.write(Rbuffer.toString());
            writer.flush();
            writer.close();
            System.out.println("RData => " + tempRoutput.getAbsolutePath());

            String[] command = {"Rscript", tempRoutput.getAbsolutePath()};
            Runtime.getRuntime().exec(command).waitFor();

            System.out.println("Image PDF => " + tempRoutputPdf.getAbsolutePath());


            Desktop.getDesktop().open(tempRoutputPdf);

        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
            e.printStackTrace();
        }

    }


}
