package org.example;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void Bai1() {
        System.out.println("Bai1");

        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        System.out.print("Nhap ban kinh r: ");
        double r = sc.nextDouble();

        int n = 1000000;
        int count = 0;

        for(int i = 0; i < n; i++) {
            double x = -r + 2 * r * rand.nextDouble();
            double y = -r + 2 * r * rand.nextDouble();

            if (x * x + y * y <= r * r) {
                count++;
            }
        }

        double area = (double) count/n * 4 * r * r;

        System.out.println("Dien tich xap xi cua hinh tron: " +area);
    }

    public static void Bai2() {
        System.out.println("\nBai2");
        Random rand = new Random();

        int n = 1000000;
        int count = 0;

        for( int i = 0; i < n; i++) {
            double x = -1 + 2 * rand.nextDouble();
            double y = -1 + 2 * rand.nextDouble();

            if (x * x + y * y <= 1) {
                count++;
            }
        }
        double pi = 4.0 * count/n;

        System.out.println("Gia tri xap xi cua pi: " +pi);
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static int cross(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    static double distance(Point a, Point b) {
        return Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
    }

    public static void Bai3() {
        System.out.println("\nBai3");
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhap so luong tram: ");
        int n = sc.nextInt();

        Point[] p = new Point[n];

        System.out.println("Nhap toa do tung tram (x,y)");
        for( int i = 0; i < n; i++) {
            System.out.print("\tNhap toa do tram " + (i+1) + ": ");
            int x = sc.nextInt();
            int y = sc.nextInt();

            p[i] = new Point(x,y);
        }

        int pivot = 0;
        for (int i = 1; i < n; i++) {
            if (p[i].y < p[pivot].y || (p[i].y == p[pivot].y && p[i].x < p[pivot].x)) {
                pivot = i;
            }
        }

        Point temp = p[0];
        p[0] = p[pivot];
        p[pivot] = temp;

        Point start = p[0];

        //Sort theo goc
        Arrays.sort(p, 1, n, (a, b) -> {
            int c = cross(start, a, b);
            if (c == 0)
                return Double.compare(distance(start, a),
                        distance(start, b));
            return -c;
        });

        //Stack
        Stack<Point> stack = new Stack<>();
        stack.push(p[0]);
        stack.push(p[1]);

        for (int i = 2; i < n; i++) {
            while (stack.size() >= 2) {
                Point top = stack.pop();
                Point next = stack.peek();

                if (cross(next, top, p[i]) > 0) {
                    stack.push(top);
                    break;
                }
            }
            stack.push(p[i]);
        }

        System.out.println("Cac tram canh bao la: ");
        for (Point pt : stack) {
            System.out.println("\tTram co toa do: " + pt.x + " " + pt.y);
        }
    }

    public static void Bai4() {
        System.out.println("\nBai4");
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhap so luong phan tu: ");
        int n = sc.nextInt();
        System.out.print("Nhap tong can tim: ");
        int k = sc.nextInt();

        int[] a = new int[n];
        System.out.print("Nhap cac phan tu: ");
        for(int i = 0; i < n; i++){
            a[i] = sc.nextInt();
        }

        int[] dp = new int[k + 1];
        int[] prev = new int[k + 1];
        int[] used = new int[k + 1];

        Arrays.fill(dp, -1);

        dp[0] = 0;

        for(int i = 0; i < n; i++){

            for(int s = k; s >= a[i]; s--){

                if(dp[s - a[i]] != -1){

                    int candidate = dp[s - a[i]] + 1;

                    if(candidate > dp[s]){
                        dp[s] = candidate;
                        prev[s] = s - a[i];
                        used[s] = a[i];
                    }
                }
            }
        }

        if(dp[k] == -1){
            System.out.println("Khong ton tai day con");
            return;
        }

        List<Integer> result = new ArrayList<>();

        int cur = k;

        while(cur > 0){
            result.add(used[cur]);
            cur = prev[cur];
        }

        Collections.reverse(result);

        for(int x : result){
            System.out.print(x + " ");
        }
    }

    public static void main(String[] args) {
        Bai1();
        Bai2();
        Bai3();
        Bai4();
    }
}