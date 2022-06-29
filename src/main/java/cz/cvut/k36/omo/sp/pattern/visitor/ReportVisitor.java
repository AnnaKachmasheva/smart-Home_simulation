package cz.cvut.k36.omo.sp.pattern.visitor;

import cz.cvut.k36.omo.sp.model.home.HomeConfiguration;

public interface ReportVisitor {

    void visit(HomeConfiguration homeConfiguration);

}