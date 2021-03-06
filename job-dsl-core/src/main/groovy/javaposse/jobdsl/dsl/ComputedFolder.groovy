package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.triggers.MultibranchWorkflowTriggerContext
import javaposse.jobdsl.dsl.helpers.workflow.OrphanedItemStrategyContext

/**
 * @since 1.58
 */
abstract class ComputedFolder extends AbstractFolder {
    protected ComputedFolder(final JobManagement jobManagement, final String name) {
        super(jobManagement, name)
    }

    @Override
    @Deprecated
    void primaryView(String primaryViewArg) {
        super.primaryView(primaryViewArg)
    }

    /**
     * Adds build triggers to the job.
     */
    void triggers(@DslContext(MultibranchWorkflowTriggerContext) Closure closure) {
        MultibranchWorkflowTriggerContext context = new MultibranchWorkflowTriggerContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.triggerNodes.each {
                project / 'triggers' << it
            }
        }
    }

    /**
     * Sets the orphaned branch strategy.
     */
    void orphanedItemStrategy(@DslContext(OrphanedItemStrategyContext) Closure closure) {
        OrphanedItemStrategyContext context = new OrphanedItemStrategyContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        if (context.orphanedItemStrategyNode != null) {
            configure { Node project ->
                Node orphanedItemStrategy = project / 'orphanedItemStrategy'
                if (orphanedItemStrategy) {
                    // there can only be only one orphanedItemStrategy, so remove if there
                    project.remove(orphanedItemStrategy)
                }

                project << context.orphanedItemStrategyNode
            }
        }
    }
}
