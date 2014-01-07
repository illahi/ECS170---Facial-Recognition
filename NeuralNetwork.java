public class NeuralNetwork{
	private static final int INPUT_SIZE= 128 *120;
	private static final int HIDDEN_LAYER_SIZE=10;
	private static final int NUMBER_OF_OUTPUTS=2;
	//private static final int NUMBER_OF_LAYERS=0;//not needed
	private static final double LEARNING_RATE= .22;
	public double error;

	public class Input_Node{
		public double input;
		public double[] weight; //array of weights coming off input node
	};

	public class Hidden_Node{
		public double input; //sum of values from input nodes
		public double sigmoid; //output of the node
		public double[] weight;
		public double error;//used during back propogation
		public double runningtotal;
	};

	public class Output_Node{
		public double input;
		public double sigmoid;
		public double error;//Used during back propogation
		public int gender;
		//public double goal;
		//May need more here
	};
	
	private Input_Node inputLayer[];
	private Hidden_Node hiddenLayer[];
	private Output_Node outputLayer[];
	//Initialize network
	
	public NeuralNetwork(){//Constructor
		inputLayer= new Input_Node[INPUT_SIZE];
		//hiddenLayer= new Hidden_Node[NUMBER_OF_LAYERS][HIDDEN_LAYER_SIZE];
		hiddenLayer= new Hidden_Node[HIDDEN_LAYER_SIZE];
		outputLayer= new Output_Node[NUMBER_OF_OUTPUTS];
	}

	public void randomizeWeights() {//Randomize the wieghts of the Input layer and hidden NUMBER_OF_LAYERS
		//Randomize input layer
		for(int i=0; i<INPUT_SIZE; i++){
			inputLayer[i]=new Input_Node();
			inputLayer[i].weight= new double[HIDDEN_LAYER_SIZE];
			for(int x=0; x<HIDDEN_LAYER_SIZE; x++){
				inputLayer[i].weight[x]=.01; //Small value could insert random function here
			}

		}


		//Randomize Hidden layer
		for(int i=0; i<HIDDEN_LAYER_SIZE; i++){
			hiddenLayer[i]= new Hidden_Node();
			hiddenLayer[i].weight= new double[NUMBER_OF_OUTPUTS];
			for(int x=0; x<NUMBER_OF_OUTPUTS; x++){
				hiddenLayer[i].weight[x]=.01; //Insert clever random function here
			}
		}

		/* Randomize output layer */
		outputLayer[0] = new Output_Node();
		outputLayer[0].gender = 0; //male
		outputLayer[1] = new Output_Node();
		outputLayer[1].gender = 1; //female
	}

	public static double sigmoid(double x){
		
		return 1 / (1 + Math.exp(-x));
		//return 1/x;
	}

	public void propogate(Image face,int type){//Pass in the Pictures values
		for(int i=0; i<INPUT_SIZE; i++)
		{
			//Copy into input layer
			 inputLayer[i].input= face.imageArr[i];
			//	face.type= type;
				//if(i==0)
				//{System.out.println("Check These Values "+ inputLayer[i].input+" "+ face.type);}
		}

		//propogate values through
		
			for(int x=0; x< HIDDEN_LAYER_SIZE; x++)
			{
				double value = 0;
				
					for(int y=0; y<INPUT_SIZE; y++){
						value += (inputLayer[y].input)*inputLayer[y].weight[x];
					}
				
				
					
			
			hiddenLayer[x].input=value;
			hiddenLayer[x].sigmoid= sigmoid(value);
		}
		for(int i=0; i<NUMBER_OF_OUTPUTS; i++){
			double value = 0.0;
			for(int y=0; y<HIDDEN_LAYER_SIZE; y++)
				{value+= hiddenLayer[y].sigmoid * hiddenLayer[y].weight[i];}
			outputLayer[i].input=value;
			outputLayer[i].sigmoid=sigmoid(value);
		/*	if (outputLayer[i].gender == face.type) {
				outputLayer[i].goal = 1;
			} else { 0 } */
			//Figure out what was expected male or female
			//Compare expected to recieved and calculate error.
			//outputLayer[i].error= Math.pow((face.type - outputLayer[i].sigmoid),2);
			//outputLayer[i].error= face.type - outputLayer[i].sigmoid;
	//		outputLayer[i].error= outputLayer[i].sigmoid*(1-outputLayer[i].sigmoid)*(face.type - outputLayer[i].sigmoid);
		//	System.out.println("Error0 "+outputLayer[i].error);
	//	System.out.println("Face Type: "+ face.type);
		outputLayer[i].error = (face.type - outputLayer[i].input) * (outputLayer[i].sigmoid) * (1 - outputLayer[i].sigmoid);
	// System.out.println("Error"+outputLayer[i].error);

		}
	}

	public void backpropogate(){
		//Start at output node
		for( int x=0; x< HIDDEN_LAYER_SIZE; x++){
			double sum =0.0;
			for(int y=0; y<NUMBER_OF_OUTPUTS; y++){
		//		sum +=outputLayer[y].error*hiddenLayer[x].weight[y];
				sum+=outputLayer[y].error;
			}	
			hiddenLayer[x].error=sum;
			//System.out.println("Hidden Layer Error"+ hiddenLayer[x].error);
		}
		//Fix weights
		//Input layer
		for(int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
			for(int x = 0; x < INPUT_SIZE; x++) {
				inputLayer[x].weight[i] += LEARNING_RATE * hiddenLayer[i].error * inputLayer[x].input;
			//	System.out.println("inputLayer weight"+ inputLayer[x].weight[i]);
			}
		}
		//Hidden layer weights updated		
		for(int x = 0; x < NUMBER_OF_OUTPUTS; x++) {
			for(int y = 0; y < HIDDEN_LAYER_SIZE; y++) {
				hiddenLayer[y].weight[x] += LEARNING_RATE * outputLayer[x].error * hiddenLayer[y].sigmoid;
			}
		}	
		//for(int x = 0; x < LAYER_SIZE_OUTPUT; x++) {
		//	for(int y = 0; y < LAYER_SIZE_HIDDEN; y++) {
		//		layerHidden[y].weights[x] += LEARNING_RATE * layerOutput[x].error * layerHidden[y].output;
	}

	public Output_Node test(Image face){//Pass in the Pictures values
		for(int i=0; i<INPUT_SIZE; i++)
		{
			//Copy into input layer
			 inputLayer[i].input= face.imageArr[i];
			if(i==0)
			{
			//System.out.println(inputLayer[i].input);
			}

		}

		//propogate values through
		
			for(int x=0; x< HIDDEN_LAYER_SIZE; x++)
			{
				double value = 0;
				
					for(int y=0; y<INPUT_SIZE; y++){
						value += inputLayer[y].input*inputLayer[y].weight[x];
					}
				
				
					
			
			hiddenLayer[x].input=value;
			//System.out.println(hiddenLayer[x].input);
			hiddenLayer[x].sigmoid= sigmoid(value);
			//System.out.println(hiddenLayer[x].sigmoid);
			//
		}
		Output_Node mostAccurate = new Output_Node();
		for(int i=0; i<NUMBER_OF_OUTPUTS; i++){
			double value = 0.0;
			mostAccurate.error= 10000;//Big number
			for(int y=0; y<HIDDEN_LAYER_SIZE; y++)
				{value+= hiddenLayer[y].sigmoid * hiddenLayer[y].weight[i];}
			outputLayer[i].input=value;
			//System.out.println("Input value"+outputLayer[i].input);
			outputLayer[i].sigmoid=sigmoid(value);
			//System.out.println("Sigmoid"+ outputLayer[i].sigmoid);
			//Figure out what was expected male or female
			//Compare expected to recieved and calculate error.
			double maletemp;
			double femaletemp;
			double maleError= Math.pow((0.5- sigmoid(value)),2);
			double femaleError= Math.pow((1-sigmoid(value)),2);
			if(maleError<femaleError){
				outputLayer[i].gender=0;
			}
			else{
				outputLayer[i].gender=1;
			}
			outputLayer[i].error= Math.pow((outputLayer[i].gender - outputLayer[i].sigmoid),2);
			if(outputLayer[i].error< mostAccurate.error){
				mostAccurate= outputLayer[i];

			}
		}
		error= mostAccurate.sigmoid/(mostAccurate.gender+.1);
		if(error>1)
		{error = 1;}
		else if(error<0)
		{
		error =0;
		}
		return mostAccurate;
	}
		
	public double Confidence()
	{
	return error;
	
	}
	
}

	
