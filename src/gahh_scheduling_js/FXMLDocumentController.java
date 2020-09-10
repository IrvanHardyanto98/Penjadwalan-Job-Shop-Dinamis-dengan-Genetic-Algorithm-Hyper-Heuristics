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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.util.Pair;
import jss.Job;
import jss.Operation;
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
    private Parent addNewJobLayout;
    private Stage addNewJobStage;
    private int jobId;
    private int threadId;
    private SchedulerMonitor monitor;
    private ExecutorService executor;

    private Schedule bestSchedule;
    
    @FXML
    private Label labelDuration;
    
    @FXML
    private Label lblMakespan;
    
    @FXML
    private Label lblMeanTardiness;
    
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
    private TableView<TableContent> jobDataTable;
    
    @FXML
    private TableView<ScheduleLegend>tableJob;
    
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
            this.problem = new Problem();
            long start = System.currentTimeMillis();
            problem.readProblem(this.inputFile.getPath());
            HashMap<Integer,Job> jobs=this.problem.getJobMap();
            ObservableList<TableContent> tableContent = FXCollections.observableArrayList();
            for (Map.Entry<Integer,Job> entry: jobs.entrySet()) {
                tableContent.add(new TableContent(entry.getKey(),entry.getValue().getReleaseDate(),entry.getValue().getDueDate(),entry.getValue().getOperationNum()));
            }
            
            this.jobDataTable.setItems(tableContent);
        }     
    }
    
    @FXML
    private void handleGenerateScheduleClick(ActionEvent event){
        //this.service.reset();
        this.labelDuration.setText("GAHH process started");
        if(this.inputFile==null){
           this.noFileSelectedAlert.show();
        }else if(!getFileExtension(this.inputFile).equals(".txt")){
            this.fileFormatAlert.show();
        }else{
            //TODO
            //Baca problem nya...
            

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
                
                this.monitor = new SchedulerMonitor(problem,this.progress,this.labelDuration, popSize, cProb, mProb, gen);
                //jalankan task awal
                this.threadId = 1;
                GAHHThread initialThread = new GAHHThread(monitor, this.threadId, 0);
                this.threadId++;
                this.executor.execute(initialThread);
                //this.executeGAHH(this.problem,popSize,cProb,mProb,gen);
                //GAHHService service = new GAHHService(popSize, gen, cProb, mProb, problem);
                
//                this.service.setParam(popSize, gen, cProb, mProb, problem);
//                if(!this.service.isRunning()){
//                    this.service.start();
//                }else{
//                    System.out.println("Service is still running!");
//                }
            }
        }

    }
    
    /**
     * referensi: http://tutorials.jenkov.com/javafx/tableview.html#customer-cell-rendering
     * https://code.makery.ch/blog/javafx-8-tableview-cell-renderer/
     */
    public void initScheduleLegendTable(){
        TableColumn<ScheduleLegend, Integer> column1 = new TableColumn<>("Job Id");
        column1.setCellValueFactory(new PropertyValueFactory<>("jobId"));
        
        TableColumn<ScheduleLegend, Color> column2 = new TableColumn<>("Color");
        column2.setCellValueFactory(new PropertyValueFactory<>("color"));
        column2.setCellFactory(tableColumn->{
            TableCell<ScheduleLegend,Color> tableCell = new TableCell<ScheduleLegend,Color>(){
                 protected void updateItem(Color item,boolean empty){
                     super.updateItem(item, empty);
                    if(empty || item ==null){
                     this.setText(null);
                    this.setGraphic(null);
                    this.setBackground(null);
                    }else{
                        this.setBackground(new Background(new BackgroundFill(item, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                 }
            };
            //tableCell.updateTableColumn(tableColumn);
            return tableCell;
        });
        
        TableColumn<ScheduleLegend, Integer> column3 = new TableColumn<>("Release Date");
        column3.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        
        TableColumn<ScheduleLegend, Integer> column4 = new TableColumn<>("Due Date");
        column4.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        TableColumn<ScheduleLegend, Integer> column5 = new TableColumn<>("Finish Time");
        column5.setCellValueFactory(new PropertyValueFactory<>("finishTime"));
        
        TableColumn<ScheduleLegend, Integer> column6 = new TableColumn<>("Tardiness");
        column6.setCellValueFactory(new PropertyValueFactory<>("tardiness"));
        
        this.tableJob.getColumns().add(column1);
        this.tableJob.getColumns().add(column2);
        this.tableJob.getColumns().add(column3);
        this.tableJob.getColumns().add(column4);
        this.tableJob.getColumns().add(column5);
        this.tableJob.getColumns().add(column6);
    }
    
    /**
     * Method ini berfungsi untuk menggambar gantt chart pada canvas
     * @author: Irvan Hardyanto, dengan referensi dari dokumen skripsi "Pembangunan Simulator Penjadwalan Flow Shop"
     */
    public void getGanttChart(Schedule sch,Problem prob){
        int makespan = sch.getMakespan();
        int jmlMesin = sch.getJmlMesin();
        
        double cnvScheduleWidth = this.cnvSchedule.getWidth();
        double cnvScheduleHeight = this.cnvSchedule.getHeight();
        
        this.conSchedule.clearRect(0, 0, cnvScheduleWidth, cnvScheduleHeight);
        this.conMachine.clearRect(0,0,this.cnvMachine.getWidth(),this.cnvMachine.getHeight());
        
        double x = 0;
        double y = 0;
        double blockHeight = cnvScheduleHeight/jmlMesin*1.0;
        double blockWidth = 0;
        
        for (int i = 0; i < jmlMesin; i++) {
            ArrayList<Schedule.Node>[] arr = sch.getSchedule();
            ArrayList<Schedule.Node> machineSequence = arr[i];
            
            for (Schedule.Node node: machineSequence) {
                int jobId = prob.getOperation(node.getOperationId()).getJobId();
                
                x = node.getStartTime()*(cnvScheduleWidth/makespan*1.0);
                blockWidth = node.getProcessingTime()*(cnvScheduleWidth/makespan*1.0);
                
                this.conSchedule.setFill(prob.getJob(jobId).getColor());
                this.conSchedule.fillRect(x,y,blockWidth,blockHeight);
            }
            y += blockHeight;
            this.conMachine.fillText("Machine: "+(i+1), 0, y-(blockHeight/2));
        }
        Platform.runLater(new Runnable(){
                    public void run(){
                        lblMakespan.setText(sch.getMakespan()+"");
                        lblMeanTardiness.setText(sch.getMeanTardiness()+"");
                    }
                });
        
        this.updateTableLegend(sch, prob);
    }
    
    public void updateTableLegend(Schedule sch,Problem prob){
        this.tableJob.getItems().clear();
        ObservableList<ScheduleLegend> item = FXCollections.observableArrayList();
        for(Map.Entry<Integer,Job> entry: prob.getJobMap().entrySet()){
            Job currJob = entry.getValue();
            int oprNum = currJob.getOperationNum();
            item.add(new ScheduleLegend(entry.getKey(), currJob.getReleaseDate(), currJob.getDueDate(), currJob.getColor(), sch.getFinishTime(currJob.getOperationIdx(oprNum-1))));
        }
        this.tableJob.getItems().addAll(item);
    }
    @FXML
    public void showAddNewJobDialog(ActionEvent event){
        this.addNewJobStage.show();
    }
    
    /*
    * Method untuk menambahkan job baru 
    */
    public void addNewJob(String[][] data,int jobNum){
        //baris ke 0, release date
        //baris ke 1, due date
        //baris ke 2, proc time
        //baris ke 3, machine assignment
        if(this.threadId==1){
            this.gahhParamAlert.setContentText("Error! tombol start scheduling belum ditekan!");
            this.gahhParamAlert.show();
        }else{
            //masalah baru, release date nya ngacak.
            //perbaikin programnya atau buat asumsi si user nge input nya ga bakal random
            //this.monitor.reschedule(Integer.parseInt(data[0][0]), data);
            this.executor.execute(new SetupThread(monitor, this.threadId, Integer.parseInt(data[0][0]), data));
            this.threadId++;
            
            HashMap<Integer,Job> tmp = this.monitor.getProblem().getJobMap();
            this.jobDataTable.getItems().clear();
            ObservableList<TableContent> tableContent = FXCollections.observableArrayList();
            for (Map.Entry<Integer,Job> entry: tmp.entrySet()) {
                tableContent.add(new TableContent(entry.getKey(),entry.getValue().getReleaseDate(),entry.getValue().getDueDate(),entry.getValue().getOperationNum()));
            }
            
            this.jobDataTable.setItems(tableContent);
            
            GAHHThread rescheduleThread = new GAHHThread(monitor, this.threadId,Integer.parseInt(data[0][0]));
            this.threadId++;
            this.executor.execute(rescheduleThread);
            ControllerMediator.getInstance().clearAll();
        }
    }
    
    @FXML
    public void handleButtonStop(ActionEvent event){
        this.threadId=1;
        executor.shutdown();  
        while (!executor.isTerminated()) {   }
        this.executor = Executors.newFixedThreadPool(5);
        this.progress.setProgress(0.0);
        this.labelDuration.setText("Ready");
    }
    
    //dipanggil saat stage(window) ditutup
    //bersihkan semua thread yang masih jalan
    public void onStop(){
        executor.shutdown();  
        while (!executor.isTerminated()) {   }  
    }
    private String getFileExtension(File f){
        int offset = f.getPath().lastIndexOf(".");
        return f.getPath().substring(offset);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddNewJob.fxml"));
            this.addNewJobLayout = loader.load();
            ControllerMediator.getInstance().registerController1(this);
            ControllerMediator.getInstance().registerController2(loader.getController());
        }catch(IOException e){
            e.printStackTrace();
        }
        this.jobId=1;
        this.problem = null;
        this.conMachine = this.cnvMachine.getGraphicsContext2D();
        this.conSchedule = this.cnvSchedule.getGraphicsContext2D();
        this.fileChooser = new FileChooser();
        this.noFileSelectedAlert =new Alert(Alert.AlertType.ERROR, "No file selected!", ButtonType.CLOSE);
        this.fileFormatAlert =new Alert(Alert.AlertType.ERROR, "Wrong file format!", ButtonType.CLOSE);
        this.gahhParamAlert = new Alert(Alert.AlertType.ERROR);
        this.executor = Executors.newFixedThreadPool(5);
        this.service = new GAHHService();
        
        this.jobDataTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("jobId"));
        this.jobDataTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        this.jobDataTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        this.jobDataTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("oprNum"));
         
        Scene s = new Scene(this.addNewJobLayout);
        this.addNewJobStage = new Stage();
        this.addNewJobStage.setScene(s);
        this.addNewJobStage.setTitle("Add New Job");
        this.labelDuration.setText("Ready");
        
        this.initScheduleLegendTable();
        
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