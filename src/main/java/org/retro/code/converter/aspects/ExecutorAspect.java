/*
 * RetroCodeConv is released under the GNU GPL v3.0 licence.
 * You may copy, distribute and modify the software as long as you keep modifications under GPL.
 * All derived works of, or applications using, RetroCodeConv must be released under the same licence and be made available to the Open Source community.
 *
 * Please refer to https://www.gnu.org/licenses/gpl-3.0.html for all licence conditions.
 *
 * Copyright © Paul C. Rau (Financial Systems Developer)
 *
 */

package org.retro.code.converter.aspects;


import org.retro.code.converter.exception.ExecutionException;
import org.retro.code.converter.execution.Executor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.retro.code.converter.xml.v1.types.XTransformFile;

/**
 * The methods in this class are called after the settings file is loaded.
 */
@Aspect
public class ExecutorAspect {
    Executor executor;

    public ExecutorAspect(Executor executor) {
        this.executor = executor;
    }

    /**
     * This aspect is executed after the setting file is loaded successfully.
     * It then calls the executor class.
     *
     * @param transformFile The settings file contents.
     */
    @AfterReturning(pointcut = "execution(* TransformLoader.load())",
    returning = "transformFile")
    public void execute(XTransformFile transformFile) throws ExecutionException {
        executor.processSettingsAndExecute(transformFile);
    }

}
