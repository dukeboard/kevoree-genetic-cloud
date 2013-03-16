
#Genetic Kevoree 4 Cloud Overview

Model@Runtime == Keep Design time information @runtime for contunious reevaluation 

	- Why contiunous reevlaution@Runtime ? => to compute adaptations face to requirements
	 for instance for elasticity

Model kept @runtime 

	- kevoree model => current of envisaged state of the architecture => in fine :-) of the cloud
	
	- sla model => user requirements , dynamic adaptation triggered by SLA changed (for example by a monitoring alerte)
		
	SLA + TypeSet (kev components) => (Search Base Engine ( Fitness & Mutator )) => Architecture Model ready to delpoy (ref Kevoree for Model@runtime causal link)

###Asumptions :
 
- Don't considering multi customers adaptations, pool of IaaS ressources pre reserved, Per Watt business model (envisaged EBRC business model)
- Don't consider dynamic variation of performance / only static SLA (if component SLA overload => non considered, if component consume less than SLA max => non considered).



#Experimental protocol

SLA (per customer) as a model @Runtime => implanted as MOF
	Expresse security level isolation and minimum CPU load need (in VCPU) of each user servcies

Case stydy : Dynamic adaptation for 
Model@Runtime layer : Kevoree => structural view of system with multinodes and topologie adapted for the cloud

Deployment and continous distributed of components of a web site
	- different data bases (customer, items, etc …)
	- frontend
	- loadbalancer
	=> component refine a service
	
	Each component Type define a security level, and max SLA consumption (expressed in VCPU)
	
	- 2 kind of infra node 
		powerfull xeon based
		arm light
		
	Each node express capacity of VCPU (internal node == 1)
				
## Problem : how to distributed components to statisfy cloud constraintes (consumtion) and sla (cpu time) constraints ?
	

#### Possible solutions : Enumeration, Search Based (GA, Greddy), Constraints Solver
	
	- Enumeration : not envisaged => nombre de solution pour un cas simple 5 noeud trop grand

or
	
	- Constraints solver) => todo in futur work
	
or 
	
	- Based on the previous result in ICSE'2012 => this experiment try to validate GA and search based for this resolution


## Question , SLA requirements and cloud consumption for instance are orthogonal and lead to a multi axial problem

###Q1
	- current legacy only consider one axis (for instance performance repartition)
		- > with Model@Runtime informations we are able to compute evalute more axis like security ?
		
	- how it impact the final result ? what about the performance to compute this ?
	
###Q2
	- what about the scalability ? for which kind of cloud size it's applicable ?
	- is random , GA composite fintess or GA epsilon dominance more suitable ?
	
Conclusion
	GA converge faster, and always before the random








	






	
