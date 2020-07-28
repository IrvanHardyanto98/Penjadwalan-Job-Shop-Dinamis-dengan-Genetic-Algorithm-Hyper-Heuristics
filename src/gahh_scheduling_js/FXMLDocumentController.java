/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import jss.Problem;
import gahh.*;
import gahh_scheduling_js.GanttChart.ExtraData;
import java.util.ArrayList;
import gahh.Schedule.Node;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Window;
/**
 *
 * @author Irvan Hardyanto
 */
public class FXMLDocumentController implements Initializable {
    private FileChooser fileChooser;
    private File inputFile;
    private Alert noFileSelectedAlert;
    private Alert fileFormatAlert;
    private Alert gahhParamAlert;
    private GAHH gahh;
    private GAHHService service;
    private GraphicsContext conMachine;
    private GraphicsContext conSchedule;
    private Problem problem;
    private String[] statusColor;

    private Schedule bestSchedule;
    
    @FXML
    private Label labelDuration;
    
    @FXML
    private Button btnBrowseFile;
    
    @FXML
    private Button btnGenSched;
    
    @FXML
    private Button btnNewJob;
    
    @FXML
    private Button btnStopScheduling;
    
    @FXML
    private Button btnNewJobDialog;
    
    @FXML
    private TableView<String> jobDataTable;
    
    @FXML
    private TextField filePathTextField;
    
    @FXML
    private TextField crossoverProb;
    
    @FXML
    private TextField mutationProb;
    
    @FXML
    private TextField genCount;
    
    @FXML
    private TextField popSize;
    
    @FXML
    private TextArea textAreaSchedule;
    
    @FXML
    private ProgressBar progress;
    
    @FXML
    private Canvas cnvSchedule;
    
    @FXML
    private Canvas cnvMachine;
    
    @FXML
    private void handleBrowseFileClick(ActionEvent event) {
        this.inputFile = null;
        this.problem = null;
        this.gahh = null;
        EventTarget target =event.getTarget();
        Stage fileChooserStage = new Stage();
        this.inputFile = this.fileChooser.showOpenDialog(fileChooserStage);
        
        if(this.inputFile != null){
            this.filePathTextField.setText(this.inputFile.getPath());
        }     
    }
    
    @FXML
    private void handleGenerateScheduleClick(ActionEvent event){
        this.service.reset();
        this.textAreaSchedule.clear();
        this.labelDuration.setText("GAHH process started");
        if(this.inputFile==null){
           this.noFileSelectedAlert.show();
        }else if(!getFileExtension(this.inputFile).equals(".txt")){
            this.fileFormatAlert.show();
        }else{
            //TODO
            //Baca problem nya...
            this.problem = new Problem();
            long start = System.currentTimeMillis();
            problem.readProblem(this.inputFile.getPath());

            String strCProb = this.crossoverProb.getText().trim();
            String strMProb = this.mutationProb.getText().trim();
            String genCount = this.genCount.getText().trim();
            String strPopSize = this.popSize.getText().trim();
            
            String strErr = "";
            if(strCProb.length()==0){
                strErr+="Crossover probability not set!\n";
            }
            if(strMProb.length()==0){
                strErr+= "Mutation probability not set!\n";
            }
            
            if(genCount.length()==0){
                strErr+= "Max Generation count not set!\n";
            }
            
            if(strPopSize.length()==0){
                strErr+= "population size not set!\n";
            }
            
            if(strErr.length() > 0){
                this.gahhParamAlert.setContentText(strErr);
                this.gahhParamAlert.show();
            }else{
                float cProb = Float.parseFloat(this.crossoverProb.getText().trim());
                float mProb = Float.parseFloat(this.mutationProb.getText().trim());
                int gen = Integer.parseInt(this.genCount.getText().trim());
                int popSize =Integer.parseInt(strPopSize);
                //this.executeGAHH(this.problem,popSize,cProb,mProb,gen);
                //GAHHService service = new GAHHService(popSize, gen, cProb, mProb, problem);
                
                this.service.setParam(popSize, gen, cProb, mProb, problem);
                if(!this.service.isRunning()){
                    this.service.start();
                }else{
                    System.out.println("Service is still running!");
                }
            }
        }

    }
    
    private void executeGAHH(Problem problem,int populationSize,float crossoverProb, float mutationProb,int maxGeneration){
        System.out.println("GAHH initiated with following parameter");
        System.out.println("population size: "+populationSize);
        System.out.println("generation count: "+maxGeneration);
        System.out.println("----------------------------------------");
         this.gahh = new GAHH(problem,populationSize,crossoverProb,mutationProb,maxGeneration);
         
         long start = System.currentTimeMillis();
        Population population = this.gahh.initPopulation(problem.getJmlJob()*problem.getJmlMesin());
        System.out.println(population.toString());
        this.gahh.evalPopulation(population);
        
        int generation = 1;
        
        while(!gahh.isTerminationConditionMet(population)){
            this.gahh.increaseGeneration();
            System.out.println("-------------------------------------");
            System.out.println("Currentgeneration: "+generation);
            
            Individual best = population.getFittest(0);
            System.out.println("Best fitness is: "+best.getFitness());
            
            System.out.println(population.toString());
            
            System.out.println("Selection + Crossover applied");
            population = gahh.onePointCrossover(population);
            
            System.out.println(population.toString());
            
            System.out.println("Mutation applied");
            population = gahh.mutatePopulation(population);
            System.out.println(population.toString());
            
            System.out.println("evaluating current population");
            this.gahh.evalPopulation(population);
            System.out.println(population.toString());
            
            
            generation++;
            System.out.println("---------------------------------------");
        }
        Individual bestIndividual = population.getFittest(0);
        this.textAreaSchedule.setText(bestIndividual.getSchedule()+"");
        System.out.println("best fitness: "+bestIndividual.getFitness());
        System.out.println("prcess take: "+(System.currentTimeMillis()-start));
        this.labelDuration.setText("Total time ellapsed: "+(System.currentTimeMillis()-start)+" ms");
    }

    public void generateGanttChart(){
        Schedule sch = this.service.getValue();
        this.getGanttChart(sch, this.problem);
        String[] row;
        String[] col;
        if(this.textAreaSchedule.getText()!=null){
            String sched = this.textAreaSchedule.getText();
            row = sched.split("\n");
            
            String[] machines = new String[row.length-2];
            for (int i = 0; i < machines.length; i++) {
                machines[i] = "Machine "+(i+1);
            }
            
            final NumberAxis xAxis = new NumberAxis();
            final CategoryAxis yAxis = new CategoryAxis();

            final GanttChart<Number,String> chart = new GanttChart<Number,String>(xAxis,yAxis);
            
            xAxis.setLabel("");
            xAxis.setTickLabelFill(Color.CHOCOLATE);
            xAxis.setMinorTickCount(4);
            
            yAxis.setLabel("");
            yAxis.setTickLabelFill(Color.CHOCOLATE);
            yAxis.setTickLabelGap(5);
            yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(machines)));

            chart.setTitle("Gantt chart Jadwal");
            chart.setLegendVisible(false);
            chart.setBlockHeight( 40);
            String machine;
            
            for(int i =0;i<row.length-2;i++){
                machine = machines[i];
                XYChart.Series series = new XYChart.Series();
                col = row[i].split("\\s+");
                for(int j = 0 ; j < col.length;j++){
                    String[] tmp = col[j].replaceAll("\\(","").replaceAll("\\)", "").split(",");
                    int startTime = Integer.parseInt(tmp[0]);
                    String id = tmp[1];
                    int endTime = Integer.parseInt(tmp[2]);
                    series.getData().add(new XYChart.Data(startTime,machine,new ExtraData(endTime-startTime,this.statusColor[Integer.parseInt(id)/machines.length],id)));
                }
                chart.getData().add(series);
            }
            
            chart.getStylesheets().add(getClass().getResource("ganttchart.css").toExternalForm());
            

            Scene scene = new Scene(chart, 1500, 1000); 
            Stage stage = new Stage(); 
            stage.setScene(scene); 
            stage.show();
        }
    }
    
    /**
     * Method ini berfungsi untuk menggambar gantt chart pada canvas
     * @author: Irvan Hardyanto, dengan referensi dari dokumen skripsi "Pembangunan Simulator Penjadwalan Flow Shop"
     */
    private void getGanttChart(Schedule sch,Problem prob){
        int makespan = sch.getMakespan();
        int jmlMesin = sch.getJmlMesin();
        
        double cnvScheduleWidth = this.cnvSchedule.getWidth();
        double cnvScheduleHeight = this.cnvSchedule.getHeight();
        
        double x = 0;
        double y = 0;
        double blockHeight = cnvScheduleHeight/jmlMesin*1.0;
        double blockWidth = 0;
        
        for (int i = 0; i < jmlMesin; i++) {
            ArrayList<Schedule.Node>[] arr = sch.getSchedule();
            ArrayList<Schedule.Node> machineSequence = arr[i];
            
            for (Schedule.Node node: machineSequence) {
                int jobId = node.getOperationIdx()/jmlMesin;
                
                x = node.getStartTime()*(cnvScheduleWidth/makespan*1.0);
                blockWidth = node.getProcessingTime()*(cnvScheduleWidth/makespan*1.0);
                
                this.conSchedule.setFill(prob.getJob(jobId).getColor());
                this.conSchedule.fillRect(x,y,blockWidth,blockHeight);
            }
            y += blockHeight;
            this.conMachine.fillText("Machine: "+(i+1), 0, y-(blockHeight/2));
        }
    }
    @FXML
    public void handleAddNewJob(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddNewJob.fxml"));
        Parent root = loader.load();
        Scene s = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(s);
        stage.setTitle("Add New Job");
        stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private String getFileExtension(File f){
        int offset = f.getPath().lastIndexOf(".");
        return f.getPath().substring(offset);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.problem = null;
        this.conMachine = this.cnvMachine.getGraphicsContext2D();
        this.conSchedule = this.cnvSchedule.getGraphicsContext2D();
        this.fileChooser = new FileChooser();
        this.noFileSelectedAlert =new Alert(Alert.AlertType.ERROR, "No file selected!", ButtonType.CLOSE);
        this.fileFormatAlert =new Alert(Alert.AlertType.ERROR, "Wrong file format!", ButtonType.CLOSE);
        this.gahhParamAlert = new Alert(Alert.AlertType.ERROR);
        this.service = new GAHHService();
        this.service.setOnSucceeded(new GAHHServiceSucceededHandler(this.textAreaSchedule));
        this.service.setOnRunning(new GAHHServiceRunningHandler(this.progress));
        this.statusColor = new String[15];
        this.statusColor[0] = "status-red";
        this.statusColor[1] = "status-light-red";
        this.statusColor[2] = "status-green";
        this.statusColor[3] = "status-blue";
        this.statusColor[4] = "status-light-blue";
        this.statusColor[5] = "status-yellow";
        this.statusColor[6] = "status-orange";
        this.statusColor[7] = "status-purple";
        this.statusColor[8] = "status-gold";
        this.statusColor[9] = "status-silver";
        this.statusColor[10] = "status-brown";
        this.statusColor[11] = "status-11";
        this.statusColor[12] = "status-12";
        this.statusColor[13] = "status-13";
        this.statusColor[14] = "status-14";
    }       
}