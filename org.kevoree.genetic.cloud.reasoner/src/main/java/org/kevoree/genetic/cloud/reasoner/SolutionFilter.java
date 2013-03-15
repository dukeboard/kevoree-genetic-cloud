package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.genetic.framework.KevoreeSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 15/03/13
 * Time: 11:31
 */
public class SolutionFilter {

    public List<KevoreeSolution> filterSolution(List<KevoreeSolution> solutions) {
        List<KevoreeSolution> result = new ArrayList<KevoreeSolution>();
        for (KevoreeSolution solution : solutions) {
            if (solution.getResultFromFitness("Completeness_Fitness") == 0d) {
                result.add(solution);
            }
        }
        return result;
    }

    public TreeSet<KevoreeSolution> order(List<KevoreeSolution> solutions) {
        TreeSet<KevoreeSolution> set = new TreeSet();
        set.addAll(solutions);
        return set;
    }

}
