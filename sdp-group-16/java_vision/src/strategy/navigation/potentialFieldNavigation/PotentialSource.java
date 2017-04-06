package strategy.navigation.potentialFieldNavigation;

import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public abstract class PotentialSource implements PotentialSourceInterface{

    protected double minX;
    protected double yFactor;
    protected boolean attract;
    protected double xCutoff;
    protected double yShift;
    protected double size;
    protected FieldFormula formula;

    public PotentialSource(FieldFormula formula, boolean attract){
        this.formula = formula;
        this.minX = 0.1;
        this.yFactor = 1;
        this.yShift = 0;
        this.xCutoff = Double.MAX_VALUE;
        this.attract = attract;
        this.size = 0;
    }

    public PotentialSource(double minX, double yFactor, boolean attract, double xCutoff, double yShift, double size){
        this.minX = Math.max(minX, 0.1);
        this.yFactor = yFactor;
        this.attract = attract;
        this.xCutoff = xCutoff;
        this.yShift  = yShift;
        this.size    = size;
    }

    public VectorGeometry relativePointToForce(VectorGeometry relativePoint){
        return relativePoint.setLength(this.getPotentialAtPoint(relativePoint));
    }

    @Override
    public double getPotentialAtPoint(VectorGeometry relativePoint){

        double adjustedDistance = Math.max(this.minX, relativePoint.length() - this.size);
        if(adjustedDistance > xCutoff) return 0;
        double funcRes = 0;
        switch(this.formula){
            case ONE_OVER_X:
                funcRes = 1/adjustedDistance;
                break;
            case ONE_OVER_X2:
                funcRes = 1/(adjustedDistance*adjustedDistance);
                break;
            case ONE_OVER_X3:
                funcRes = 1/(adjustedDistance*adjustedDistance*adjustedDistance);
                break;
            case E_TO_MINUS_X:
                funcRes = Math.pow(Math.E, -adjustedDistance);
                break;
            case MINUS_LN_X:
                funcRes = -Math.log(adjustedDistance);
                break;
        }
        funcRes = yFactor*funcRes + yShift;
        return Math.max(funcRes, 0) * (attract ? -1 : 1);
    }

    public PotentialSource setMinX(double minX) {
        this.minX = minX;
        return this;
    }

    public PotentialSource setYFactor(double yFactor) {
        this.yFactor = yFactor;
        return this;
    }

    public PotentialSource setAttract(boolean attract) {
        this.attract = attract;
        return this;
    }

    public PotentialSource setxCutoff(double xCutoff) {
        this.xCutoff = xCutoff;
        return this;
    }

    public PotentialSource setyShift(double yShift) {
        this.yShift = yShift;
        return this;
    }

    public PotentialSource setFormula(FieldFormula formula) {
        this.formula = formula;
        return this;
    }

    public PotentialSource setSize(double size) {
        this.size = size;
        return this;
    }
}
