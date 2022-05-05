package tokyo.nakanaka.buildvox.core.math;

/**
 * Has methods to calculate max or min of several doubles.
 */
public class MaxMinCalculator {
    private MaxMinCalculator(){
    }

    /**
     * Calculate the max of the given doubles
     * @param nums the doubles to calculate the max
     * @throws IllegalArgumentException if nums length is 0
     */
    public static double max(double... nums){
        if(nums.length == 0){
            throw new IllegalArgumentException();
        }
        double m = nums[0];
        for (double num : nums) {
            if (num > m) {
                m = num;
            }
        }
        return m;
    }

    /**
     * Calculate the min of the given doubles
     * @param nums the doubles to calculate the min
     * @throws IllegalArgumentException if nums length is 0
     */
    public static double min(double... nums){
        if(nums.length == 0){
            throw new IllegalArgumentException();
        }
        double m = nums[0];
        for (double num : nums) {
            if (num < m) {
                m = num;
            }
        }
        return m;
    }

}
