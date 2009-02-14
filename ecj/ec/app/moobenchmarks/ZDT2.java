package ec.app.moobenchmarks;

import ec.*;
import ec.util.*;
import ec.vector.*;
import ec.simple.*;
import ec.multiobjective.*;

/**
 * 
 * ZDT-T2 nonconvex counterpart to ZDT-T1.
 * 
 * <p>The Pareto-optimal front is formed with g(X) = 1.
 * 
 * <p>Zitzler, E., Deb, K., and Thiele, L., 2000, Comparison of Multiobjective Evolutionary
 * Algorithms: Empirical Results, Evolutionary Computation, Vol. 8, No. 2, pp173-195.
 * 
 * <p><b>Parameters</b><br>
 * <table>
 * <tr><td valign=top><i>base</i>.<tt>numvariables</tt><br>
 * <font size=-1>int (default=30)</font></td>
 * <td valign=top>The number of variables; genome-size is set to this value internally.</td></tr>
 * </table>
 * 
 * @author  Gabriel Balan
 */
public class ZDT2 extends Problem implements SimpleProblemForm
{
	public int numDecisionVars;
	public static final String P_NUMVARS = "numvariables"; 


    public void setup(final EvolutionState state, final Parameter base) 
	{
		super.setup(state, base);
		Parameter def = defaultBase();

		numDecisionVars = state.parameters.getIntWithDefault(base.push(P_NUMVARS), def.push(P_NUMVARS),30);
		if (numDecisionVars< 2)
			state.output.fatal("The number of variables must be an integer > 1", base.push(P_NUMVARS), def.push(P_NUMVARS));

		/**
		 * I want to force the following values:
		 * 
		 * pop.subpop.0.species = ec.vector.FloatVectorSpecies
		 * pop.subpop.0.species.ind = ec.vector.FloatVectorIndividual
		 * pop.subpop.0.species.min-gene = 0
		 * pop.subpop.0.species.max-gene = 1
		 * pop.subpop.0.species.genome-size = numDecisionVars //could be anything, should be 30
		 */
		state.parameters.set(new Parameter("pop.subpop.0.species"),"ec.vector.FloatVectorSpecies");
		state.parameters.set(new Parameter("pop.subpop.0.species.ind"),"ec.vector.FloatVectorIndividual");
		state.parameters.set(new Parameter("pop.subpop.0.species.genome-size"),""+numDecisionVars);
		state.parameters.set(new Parameter("pop.subpop.0.species.min-gene"),"0");
		state.parameters.set(new Parameter("pop.subpop.0.species.max-gene"),"1");
		state.parameters.set(new Parameter("pop.subpop.0.species.fitness.numobjectives"),"2");
	}
	
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum)
	{
		if(ind.evaluated)
			return;
		float[] genome = ((FloatVectorIndividual)ind).genome;
		float[] fitnesses = ((MultiObjectiveFitness)ind.fitness).multifitness;
		float f = fitnesses[0] = genome[0];
		float sum = 0;
		for(int i = 1; i< numDecisionVars; ++i)
			sum += genome[i];
		double g = 1d+9d*sum/(numDecisionVars-1);
		double h = 1d-(f/g)*(f/g);
		fitnesses[1] = (float)(g*h);
		ind.evaluated = true;
	}
    public void describe(final Individual ind, 
            final EvolutionState state, 
            final int subpopulation,
            final int threadnum,
            final int log,
            final int verbosity)
    {}
}