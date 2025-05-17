package org.example;

import java.io.*;
import java.util.Scanner;

public class Main{

	static Scanner sc = new Scanner(System.in);

	private static int[][]maze;

	static long startTime = System.currentTimeMillis();

	static int[] playerPos;

	static int size;

	static int[] endPos;

	private static int stepCount = 0;

	private static State state = new State();

	private static String[][] gameState;

	private static int currentLevel;

	private static boolean[] completions = new boolean[Map.levels];

	private static void move(int x, int y){
		gameState[playerPos[0]][playerPos[1]] = Models.passModel;
		playerPos[0] += y;
		playerPos[1] += x;
		gameState[playerPos[0]][playerPos[1]] = Models.playerModel;

	}

	private static void chooseLevel(){

		System.out.print("Выберите уровень:\n");
		for(int i = 1; i < Map.levels; ++i){
			System.out.printf("%d. %dx%d %s\n", i, Map.getSize(i), Map.getSize(i), completions[i] ? "(Пройден)" : "(Не пройден)");
		}

		while (true) {
			String inp = sc.next();
			if (inp.equals("load")) {
				load();
				chooseLevel();
				return;
			}
			try{
				int level = Integer.parseInt(inp);
				maze = Map.getMaze(level);
				playerPos = Map.getPlayerPos(level);
				endPos = Map.getEndPos(level);
				size = Map.getSize(level);
				currentLevel = level;

				gameState = new String[size][size];
				for(int i = 0;i<size;i++){
					for(int j = 0;j<size;j++){
						gameState[i][j] = Models.voidModel;
					}
				}
				gameState[playerPos[0]][playerPos[1]] = Models.playerModel;
				gameState[endPos[0]][endPos[1]] = Models.finishModel;
				printGameField();

			} catch (Exception e) {
				System.out.println("Такой уровень не найден");
				continue;
            }
			break;
        }
	}

	private static void startGame(){

		for(int i = 0;i<size;i++){
			for(int j = 0;j<size;j++){
				gameState[i][j] = Models.voidModel;
			}
		}

		gameState[playerPos[0]][playerPos[1]] = Models.playerModel;
		gameState[endPos[0]][endPos[1]] = Models.finishModel;
		state.updateState();
		printGameField();

		while(true){
			String inp = sc.next().toLowerCase();
			stepCount+=1;
			int[] curPos = playerPos;

			switch(inp) {
				case ("w"):
					if (curPos[0] != 0 && maze[curPos[0]-1][curPos[1]] != 1) {
						move(0,-1);
					} else if (playerPos[0] - 1 >= 0) {
						gameState[playerPos[0]-1][playerPos[1]] = Models.wallModel;
					}
					break;
				case ("s"):
					if (curPos[0] != size - 1 && maze[curPos[0]+1][curPos[1]] != 1) {
						move(0,1);
					} else if (playerPos[0] + 1 < size) {
						gameState[playerPos[0]+1][playerPos[1]] = Models.wallModel;
					}
					break;
				case ("a"):
					if (curPos[1] != 0 && maze[curPos[0]][curPos[1]-1] != 1) {
						move(-1,0);
					} else if (playerPos[1] - 1 >= 0) {
						gameState[playerPos[0]][playerPos[1]-1] = Models.wallModel;
					}
					break;
				case ("d"):
					if (curPos[1] != size - 1 && maze[curPos[0]][curPos[1]+1] != 1) {
						move(1,0);
					} else if (playerPos[1] + 1 < size) {
						gameState[playerPos[0]][playerPos[1]+1] = Models.wallModel;
					}
					break;
				case ("save"):
					save();
					break;
				case ("load"):
					load();
					break;
				case("exit"):
					chooseLevel();
					break;
				case("rules"):
					printRules();
					break;
				default:
					System.out.println("Неверная команда");
					break;
			}
			printGameField();

			if (playerPos[0] == endPos[0] & playerPos[1] == endPos[1]) {
				completions[currentLevel] = true;
				save();
				printResult();
				chooseLevel();
				continue;
			}
		}

	}

	private static void printGameField(){
		System.out.println("Время игры: " + (System.currentTimeMillis() - startTime)/1000 + " секунд");
		for (int i = 0;i<gameState[0].length;i++){
			for (int j = 0;j<gameState[1].length;j++){
				System.out.print(" "+gameState[i][j]+" ");
			}
			System.out.println();
		}
	}

	private static void printRules(){

		System.out.print("Игра - Слепой лабиринт\n" +
				Models.playerModel + " - игрок\n" +
				Models.finishModel + " - финиш\n" +
				Models.passModel + " - пройденная клетка\n" +
				Models.wallModel + " - стена\n" +
				"Правила: Вводи в консоль символы wasd, чтобы передвигаться\n" +
				"w - вверх\n" +
				"a - влево\n" +
				"s - вниз\n" +
				"d - вправо\n" +
				"При ударе о стену, она отобразится на экране\n" +
				"Команды:\n" +
				"save - сохранить игру\n" +
				"load - загрузить сохранение\n" +
				"rules - прочитать правила\n" +
				"exit - вернуться к выбору уровня(не забудь сохраниться)\n");

		System.out.printf("Введи любой символ чтобы продолжить...\n");;
		sc.next();
	}
	private static void printResult(){
		long endTime = System.currentTimeMillis();

		System.out.printf("Лабиринт %s пройден за %d шага(-ов) \n", currentLevel, stepCount);
		System.out.printf("Затраченное время: %d секунд(-ы)\n",(endTime-startTime)/1000);;
		//System.out.printf("Игра была автоматически сохранена\n");
		System.out.printf("Введи любой символ чтобы перейти в меню уровней...\n");;

		sc.next();
	}



	static class State implements Serializable{
		String[][] gameState;
		int[][] maze;
		int[] playerPos;
		int[] endPos;
		int size;
		int stepCount;
		long saveTime;
		long startTime;
		int currentLevel;
		boolean[] completions;

		public void updateState(){
			maze = Main.maze;
			gameState = Main.gameState;
			playerPos = Main.playerPos;
			endPos = Main.endPos;
			size = Main.size;
			stepCount = Main.stepCount;
			startTime = Main.startTime;
			currentLevel = Main.currentLevel;
			completions = Main.completions;
			saveTime = System.currentTimeMillis();
		}
	}



	private static void save(){
		state.updateState();
		try(
				FileOutputStream fos = new FileOutputStream("SaveFile.save");
				ObjectOutputStream oos = new ObjectOutputStream(fos)){
			oos.writeObject(state);
			oos.flush();
			System.out.println("Игра сохранена");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void load(){
		try(
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream("SaveFile.save"))){
			state = (State) ois.readObject();
			maze = state.maze;
			gameState = state.gameState;
			playerPos = state.playerPos;
			endPos = state.endPos;
			stepCount = state.stepCount;
			currentLevel = state.currentLevel;
			completions = state.completions;
			startTime = System.currentTimeMillis() - (state.saveTime - state.startTime);
			System.out.println("Игра загружена");
		}
		catch (ClassNotFoundException | IOException e) {
			System.out.println("Сохранение не найдено");
		}
	}

	public static void main(String[] args){
		printRules();
		chooseLevel();
		startGame();

	}
}