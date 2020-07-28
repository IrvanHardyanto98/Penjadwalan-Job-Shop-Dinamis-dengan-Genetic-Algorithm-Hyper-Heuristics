package jss;
import javafx.scene.paint.Color;
/**
 * 
 * @author 
 */
public class Job{
	private int releaseDate;
	private int dueDate;
//	private int weight;
        private Color color;
	private int operationNum;
	private int[] operationId;
	
	public Job(int releaseDate,int dueDate,int operationNum){
		this.releaseDate = releaseDate;
		this.dueDate = dueDate;
//		this.weight = weight;
		this.operationNum = operationNum;
		this.operationId = new int[operationNum];
                this.color = Color.color(Math.random(),Math.random(),Math.random());
	}
	
	public int getReleaseDate(){
		return this.releaseDate;
	}
	
	public void setReleaseDate(int releaseDate){
		this.releaseDate = releaseDate;
	}
	
	public int getDueDate(){
		return this.dueDate;
	}
	
	public void setDueDate(int dueDate){
		this.dueDate = dueDate;
	}
        
//        public int getWeight(){
//            return this.weight;
//        }
//        public void setWeight(int weight){
//            this.weight=weight;
//        }
	
        public Color getColor(){
            return this.color;
        }
        
        public void setColor(Color col){
            this.color = col;
        }
        
	public int getOperationNum(){
		return this.operationNum;
	}
	
	public int getOperationIdx(int oprNum){
		return this.operationId[oprNum];
	}
	
	public void setOperationIdx(int oprNum,int oprIdx){
		this.operationId[oprNum]=oprIdx;
	}
	
	public int[] getOperationIdx(){
		return this.operationId;
	}
	
	public void setOperationIdx(int[] operationIdx){
		this.operationId=operationIdx;
	}
}