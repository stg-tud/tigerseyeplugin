package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.pointcuts.Pointcut;

public interface ILogoPointcutLanguage {
	
	Pointcut pmotion();
	
	Pointcut pturning();
	
	Pointcut pturning(int degrees);
	
	Pointcut pturning(int minDegrees, int maxDegrees);
	
    Pointcut pleft();	

    Pointcut pleft(int degrees);	

	Pointcut pleft(int minDegrees, int maxDegrees);

	Pointcut pright();	

    Pointcut pright(int degrees);
    
	Pointcut pright(int minDegrees, int maxDegrees);

	Pointcut pmoving();
	
	Pointcut pmoving(int steps);
	
	Pointcut pmoving(int minSteps, int maxSteps);

	Pointcut pforward();

    Pointcut pforward(int steps);
    
	Pointcut pforward(int minSteps, int maxSteps);

	Pointcut pbackward();

    Pointcut pbackward(int steps);
    
    Pointcut pbackward(int minSteps, int maxSteps);
}
