package bgu.spl.mics.application;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** This is the Main class of the application. You should parse the input file, 
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static CountDownLatch countDownLatch;
	// countDownLatch is a mechanism that will help us synchronize Leia's initialize method

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("please enter input file path");
		String inputPath = scanner.nextLine();
		System.out.println("please enter output file path");
		String outputPath = scanner.nextLine();
		countDownLatch = new CountDownLatch(4);
		try {
			inputPath = "C:\\Users\\nivyo\\OneDrive\\שולחן העבודה\\github_projects\\Message-bus\\input.json";
			outputPath = "C:\\Users\\nivyo\\OneDrive\\שולחן העבודה\\github_projects\\Message-bus\\output.json";

			// parse input
			Input input = JsonInputReader.getInputFromJson(inputPath);

			// initialize micro services
			MicroService C3P0 = new C3POMicroservice();
			MicroService HanSolo = new HanSoloMicroservice();
			LandoMicroservice Lando = new LandoMicroservice();
			R2D2Microservice R2D2 = new R2D2Microservice();
			LeiaMicroservice Leia = new LeiaMicroservice(input.getAttacks(), input.getR2D2(), input.getLando());

			Diary diary = Diary.getInstance();
			Ewoks ewoks = Ewoks.getInstance(); // get ewoks (for the attack events) singleton
			for(int i = 1; i <= input.getEwoks(); i++){
				Ewok ewok = new Ewok(i);
				ewoks.addEwok(ewok);
			}

			Thread c3poThread = new Thread(C3P0);
			Thread hanSoloThread = new Thread(HanSolo);
			Thread r2d2Thread = new Thread(R2D2);
			Thread landoThread = new Thread(Lando);
			Thread leiaThread = new Thread(Leia);
			c3poThread.start();
			hanSoloThread.start();
			r2d2Thread.start();
			landoThread.start();
			leiaThread.start();

			try{ // wait until all threads will finish their run method (until a terminate broadcast is sent)
				c3poThread.join();
				hanSoloThread.join();
				r2d2Thread.join();
				landoThread.join();
				leiaThread.join();
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			// generating output file
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileWriter writer = new FileWriter(outputPath);

			gson.toJson(diary,writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("exit");
	}
}
