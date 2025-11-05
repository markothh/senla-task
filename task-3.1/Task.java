public class Task {
    public static void main() {
        int num = (new java.util.Random()).nextInt(100, 1000);
        int[] nums = {num/100, num/10%10, num%10};

        System.out.printf("Случайное число: %d%n", num);
        System.out.printf("Наибольшая цифра: %d", Math.max(nums[0], Math.max(nums[1], nums[2])));
    };
};
