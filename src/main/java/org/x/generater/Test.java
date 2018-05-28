package org.x.generater;

public class Test {

	public static void main(String[] args) {
		int[] arr = { 6, 3, 8, 2, 2, 9, 1 };
		for (int i = 0; i < arr.length - 1; i++) {// 外层循环控制排序趟数
			for (int j = 0; j < arr.length - 1 - i; j++) {// 内层循环控制每一趟排序多少次
				if (arr[j] > arr[j + 1]) {
					int temp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = temp;
				}
			}
		}
		System.out.println("排序后的数组为：");
		for (int num : arr) {
			System.out.print(num + " ");
		}
	}
	
	
	public static void sort(int[] arr) {
		int temp;
		for (int i = 0; i < arr.length -1; i++) {
			for (int j = 0; j < arr.length -(++i); j++) {
				if(arr[j] > arr[j +1]) {
					temp = arr[j];
					arr[j] =arr[j +1];
					arr[j + 1]  = temp;
				}
			}
		}
		for (int i = 0; i < arr.length; i++) {
			System.err.println(arr[i]);
		}
	}

}
