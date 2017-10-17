package org;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

import ptolemy.actor.TypedIOPort;
import ptolemy.actor.lib.LimitedFiringSource;
import ptolemy.actor.parameters.PortParameter;
import ptolemy.actor.util.ActorTypeUtil;
import ptolemy.actor.util.ArrayElementTypeFunction;
import ptolemy.data.ArrayToken;
import ptolemy.data.DoubleToken;
import ptolemy.data.StringToken;
import ptolemy.data.expr.Parameter;
import ptolemy.data.expr.Token;

import ptolemy.data.type.BaseType;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.Attribute;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.InternalErrorException;
import ptolemy.kernel.util.NameDuplicationException;

import ptolemy.kernel.util.Workspace;
import ptolemy.data.IntToken;

public class Mva extends LimitedFiringSource {
	public Parameter username;
	public Parameter outt;
	public Parameter attrib;
	public TypedIOPort input;
	public TypedIOPort outNc;
	public TypedIOPort outK;
	public PortParameter arrayLength;
	

	

	public Mva(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		_firingCountLimit=1;
		
		input = new TypedIOPort(this, "input", true, false);
		outNc = new TypedIOPort(this, "outNc", false, true);
		outK = new TypedIOPort(this, "outK", false, true);
		outt = new Parameter(this, "file Number");
        outt.setTypeEquals(BaseType.DOUBLE);
        
        input.setMultiport(true);
        input.setAutomaticTypeConversion(true);
        output.setTypeEquals(BaseType.DOUBLE);
        outNc.setTypeEquals(BaseType.DOUBLE);
        outK.setTypeEquals(BaseType.DOUBLE);
        
	   
		  
        
        
		username = new Parameter(this, "username", new DoubleToken());
		// TODO Auto-generated constructor stub
			
	}
	
	@Override
    public void attributeChanged(Attribute attribute)
            throws IllegalActionException {
        if (attribute == arrayLength) {
            int rate = ((IntToken) arrayLength.getToken()).intValue();

            if (rate < 0) {
                throw new IllegalActionException(this, "Invalid arrayLength: "
                        + rate);
            }
        } else {
            super.attributeChanged(attribute);
        }
    }
	
	
	public Object clone(Workspace workspace) throws CloneNotSupportedException {
        Mva newObject = (Mva) super.clone(workspace);
        try {
            newObject.output.setTypeAtLeast(ActorTypeUtil.arrayOf(
                    newObject.input, newObject.arrayLength));
            newObject.input.setTypeAtLeast(new ArrayElementTypeFunction(
                    newObject.output));
        } catch (IllegalActionException e) {
            throw new InternalErrorException(e);
        }
        return newObject;
    }
	//Fonction qui retourne le type d'un fichier
	public static String type_fichier(String fichier){
		fichier = fichier.substring(0, fichier.length());
		int pos = fichier.lastIndexOf(".");
		if (pos > -1) {
			return fichier.substring(pos);

		} else {
			return fichier;
		}

	}
	
	
	//fonction qui lit le nombre de server et le nombre de client
	public static int[] Nombre_De_ServerEtClient(String fichier,String type) throws IOException{
		   
		File f = new File(fichier);
		BufferedReader br = new BufferedReader(new FileReader(f));
		int[] x = new int[2];
		if ((type.charAt(1) == 't' && type.charAt(2) == 'g' && type.charAt(3) == 'f')
				&& (type.charAt(1) == 't' && type.charAt(2) == 'x' && type.charAt(3) == 't'))
			JOptionPane.showMessageDialog(null, "format de fichier incorrect");

		if ((type.charAt(1) == 't' && type.charAt(2) == 'g' && type.charAt(3) == 'f')) {
			String line;
			int i = 0;
			line = br.readLine();

			while (line.charAt(0) != '#') {
				String[] Q = line.split(" ");
				line = br.readLine();
				i++;
			}
			x[0] = i;
			String NC = JOptionPane.showInputDialog("Nombre de Clients : ");
			x[1] = Integer.parseInt(NC);
		} else if (type.charAt(1) == 't' && type.charAt(2) == 'x' && type.charAt(3) == 't') {

			String line;
			line = br.readLine();
			x[0] = Integer.parseInt(line);
			line = br.readLine();
			x[1] = Integer.parseInt(line);
		}   
	   return x;
	
	}
	
	//fonction qui lit la matrice de transition
	public static double[][] Matrice_de_transition(String file,String type,int M) throws IOException{
		
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		double[][] p = new double[M + 1][M + 1];
		String line;
		if (type.charAt(1) == 't' && type.charAt(2) == 'g' && type.charAt(3) == 'f') {
			int i = 0;
			line = br.readLine();
			double[] c = null;
			while (line.charAt(0) != '#') {
				line = br.readLine();
			}

			for (int j = 0; j < i; j++) {
				for (int d = 0; d < i; d++) {
					p[j][d] = 0;
				}
			}
			line = br.readLine();
			while (line != null) {
				String[] Q = line.split(" ");
				int j = Integer.parseInt(Q[0]) - 1;
				int d = Integer.parseInt(Q[1]) - 1;
				p[j][d] = Double.valueOf(Q[Q.length - 1]);
				line = br.readLine();

			}
		} else if (type.charAt(1) == 't' && type.charAt(2) == 'x' && type.charAt(3) == 't') {

			int H = 0;

			line = br.readLine();
			line = br.readLine();
			line = br.readLine();
			line = br.readLine();
			line = br.readLine();
			while ((line = br.readLine()) != null) {

				String[] D = line.split(" ");
				double c = 0;
				for (int i = 0; i < D.length; i++) {
					c = c + Double.valueOf(D[i]);
					p[H][i] = Double.valueOf(D[i]);
				}
				if (c < 0.9999 || c > 1) {

					JOptionPane.showMessageDialog(null, "erreur: La somme de la ligne " + H + " est differente de 1");

				}
				H++;

			}

		}
		return p;

	}
	
	//Fonction qui lis le service ds servers
	public static double[] service(String file,String type,int M) throws IOException{
		File f = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(f));

		String line;
		double[] p = new double[M + 1];
		if (type.charAt(1) == 't' && type.charAt(2) == 'g' && type.charAt(3) == 'f') {
			int i = 0;

			line = br.readLine();

			while (line.charAt(0) != '#') {
				String flot = "";
				String[] Q = line.split(" ");
				for (int z = 0; z < Q.length; z++) {
					if ((Q[z].charAt(0) == 'M' || Q[z].charAt(0) == 'm')
							&& (Q[z].charAt(1) == 'U' || Q[z].charAt(1) == 'u')) {
						for (int j = 0; j < Q[z + 1].length(); j++) {
							if (Q[z + 1].charAt(j) == ',')
								flot = flot + '.';
							else
								flot = flot + Q[z + 1].charAt(j);
						}

						p[i] = Double.valueOf(flot);
					}

				}

				line = br.readLine();
				i++;
			}

		} else if (type.charAt(1) == 't' && type.charAt(2) == 'x' && type.charAt(3) == 't') {

			line = br.readLine();
			line = br.readLine();
			line = br.readLine();
			String[] Q = line.split(" ");

			for (int i = 0; i < Q.length; i++) {
				p[i] = Double.valueOf(Q[i]);
			}
		}
		return p;
	}
	
	public static int[] Nbserver(String file, String type, int M) throws IOException {
		File f = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(f));
		int[] D = new int[M + 1];
		String line = "";
		if (type.charAt(1) == 't' && type.charAt(2) == 'g' && type.charAt(3) == 'f') {
			line = br.readLine();
			int i = 0;
			while (line.charAt(0) != '#') {
				String[] Q = line.split(" ");

				for (int z = 0; z < Q.length; z++) {
					if ((Q[z].charAt(0) == 'm' || Q[z].charAt(0) == 'M')
							&& (Q[z].charAt(1) == 'i' || Q[z].charAt(1) == 'I'))
						D[i] = Integer.parseInt(Q[z + 1]);

				}
				i++;
				line = br.readLine();
			}
		} else if (type.charAt(1) == 't' && type.charAt(2) == 'x' && type.charAt(3) == 't') {

			int H = 0;

			line = br.readLine();
			line = br.readLine();
			line = br.readLine();
			line = br.readLine();
			line = br.readLine();
			String[] P = line.split(" ");
			for (int z = 0; z < P.length; z++) {
				D[z] = Integer.parseInt(P[z]);
			}

		}
		return D;

	}

	public static int[] Type(String fichier, String type, int M) throws IOException {
		File f = new File(fichier);
		BufferedReader br = new BufferedReader(new FileReader(f));
		int[] D = new int[M + 1];
		String line = "";
		if (type.charAt(1) == 't' && type.charAt(2) == 'g' && type.charAt(3) == 'f') {
			line = br.readLine();
			int i = 0;
			while (line.charAt(0) != '#') {
				String[] Q = line.split(" ");

				for (int z = 0; z < Q.length; z++) {
					if ((Q[z].charAt(0) == 't' || Q[z].charAt(0) == 'T')
							&& (Q[z].charAt(1) == 'Y' || Q[z].charAt(1) == 'y')
							&& (Q[z].charAt(2) == 'P' || Q[z].charAt(2) == 'p')
							&& (Q[z].charAt(3) == 'E' || Q[z].charAt(3) == 'e'))
						D[i] = Integer.parseInt(Q[z + 1]);

				}
				i++;
				line = br.readLine();
			}
		} else if (type.charAt(1) == 't' && type.charAt(2) == 'x' && type.charAt(3) == 't') {

			int H = 0;

			line = br.readLine();
			line = br.readLine();
			line = br.readLine();
			line = br.readLine();
			String[] P = line.split(" ");
			for (int z = 0; z < P.length; z++) {
				D[z] = Integer.parseInt(P[z]);
			}

		}
		return D;
	}

	public static double[] GTH(double[][] P, int M) {
		double[] Pi = new double[M];
		// **************** GTH ************************

		for (int n = M - 1; n >= 1; n--) {
			double S = 0;
			for (int j = 0; j <= n - 1; j++) {
				S = S + P[n][j];
				System.out.println("S "+S);
			}

			for (int i = 0; i <= n - 1; i++) {
				P[i][n] = P[i][n] / S;
			}
			for (int i = 0; i <= n - 1; i++) {
				for (int j = 0; j <= n - 1; j++) {
					P[i][j] = P[i][j] + (P[i][n] * P[n][j]);

				}
			}
			
		}

		Pi[0] = 1;
		double TOT = 1;

		for (int j = 1; j <= M - 1; j++) {
			Pi[j] = P[0][j];
			for (int k = 1; k <= j - 1; k++) {
				Pi[j] = Pi[j] + Pi[k] * P[k][j];
			}

			TOT = TOT + Pi[j];

		}

		for (int j = 0; j <= M - 1; j++) {

			Pi[j] = Pi[j] / TOT;
			
		}

		return Pi;
	}

	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		
		
        

		String fichier = ((StringToken) input.get(0)).toString();
		// remove the quotes
		fichier = fichier.substring(1, fichier.length() - 1);
		File file = new File(fichier);
	
		try {
			if (file.exists() != true)
				JOptionPane.showMessageDialog(null, "Fichier introuvable");
			else {
				BufferedReader br = new BufferedReader(new FileReader(file));

				int[] NC = null;
				double[][] P = null;/*{ { 1, 1, 0, 0, 0, 0, 0 }, { 0, -3, 1, 0, 2, 0, 0 }, { 0, 0, -3, 1, 0, 2, 0 },
						{ 0, 0, 0, -2, 0, 0, 2 }, { 2, 0, 0, 0, -3, 1, 0 }, { 0, 2, 0, 0, 0, -3, 1 },
						{ 0, 0, 2, 0, 0, 0, -2 } }*/;
				double[] mu = null;
				int[] typeServer = null;
				int[] NbServer = null;

				String type = type_fichier(fichier);
				NC = Nombre_De_ServerEtClient(fichier, type);

				int M = NC[0];
				int N = NC[1];
				mu = service(fichier, type, M);
				P = Matrice_de_transition(fichier, type, M);
				
				typeServer = Type(fichier, type, M);
				NbServer = Nbserver(fichier, type, M);
				
				double[] Nc = new double[M]; // # moyene de client dans la
				double[] T = new double[M]; // temp moyene de reponse
				double[] X = new double[M]; // A(i)
				double[] V = GTH(P, M);				

				NumberFormat fmat = NumberFormat.getNumberInstance();
				fmat.setMaximumFractionDigits(3);
				fmat.setMinimumFractionDigits(3);

				int numLD = 0;
				for (int i = 1; i <= M; i++) {
					if (typeServer[i] == 2) {
						numLD++;
					} // Determine number of LD nodes
				}
				double[][] pMarg = new double[N + 1][numLD + 1];
				for (int m = 0; m <= numLD; m++) {
					pMarg[0][m] = 1; // Marginals for LD nodes
				}
			
				 //int width = output.getWidth();
				String Ks="";
				double[][] Tvalue= new double[N][M];
				
				for (int k=1; k<=N; k++){
					
//				       ***  MVA Step 1:  Compute mean response times  ***
				         int LDcount = 0;                                   // Indicator for LD nodes
				         
				         for (int m=0 ; m < M; m++) {
				        	  
				            if (typeServer[m] == 3) { T[m] = 1/mu[m]; }          // Infinite server node
				            if (typeServer[m] == 1) { T[m] = (Nc[m]+1)/mu[m]; }   // FIFO exponential, etc.
				            if (typeServer[m] == 2) {                           
				               T[m] = 0;                                   
				               for (int j=1; j <=k; j++) {                  
				                   // In this implementation, alpha_i(j) is equal to min(m_i, j)
				                   double alpha = j; if (NbServer[m] < j){alpha = NbServer[m];}
				                   T[m] = T[m] + j/(mu[m] * alpha) * pMarg[j-1][LDcount];
				                   
				                  }
				              LDcount++;
				            }
				            Tvalue[k-1][m]=T[m];
				            
					        
				         }  
				         String attribut=outt.getExpression();
				         System.out.println(attribut);
				         output.send(0, new DoubleToken(Tvalue[k-1][Integer.parseInt(attribut)]));
				         double[][] Nvalue= new double[N][M];
				         double[][] Xvalue= new double[N][M];
//				       ***  MVA Step 2:   Compute throughputs
				         double bottom = 0;
				         for (int m=0; m<M; m++){
				             bottom = bottom + V[m]*T[m];
				         }
				         X[0] = k/bottom;                          // Overall throughput
				         for (int m=1; m<M; m++) {
				             X[m] = V[m]*X[0];                     // Throughput of node m
				             Xvalue[k-1][m]= X[m];
				             
				         }outK.send(0, new DoubleToken(k));

//				       *** MVA Step 3:   Compute mean number of customers  ***
				         
				         for (int m=0; m< M; m++) {
				            Nc[m] = X[m]*T[m];
				            Nvalue[k-1][m]= Nc[m];
				            }outNc.send(0, new DoubleToken(Nvalue[k-1][Integer.parseInt(attribut)]));

//				               Compute marginals for all load-dependent nodes
				         LDcount = 0;                              //  Indicator for LD nodes
				         for (int m=0; m<M; m++) {
				             if (typeServer[m] == 2) {
				                double B = 0;
				                for (int j=k; j >=1; j--) {
				                    // In this implementation, alpha_i(j) is equal to min(m_i, j)
				                    double alpha = j; if (NbServer[m] < j){alpha = NbServer[m];}
				                    pMarg[j][LDcount] = X[m]/(mu[m] * alpha) * pMarg[j-1][LDcount];
				                    B = B + pMarg[j][LDcount];
				                } 
				                pMarg[0][LDcount] = 1-B;
				                LDcount++;
				            }
				         }
				        
				   
//				              Output results
				      System.out.println("     " );
				      System.out.println("  k = " + k);
						Ks=Ks+"  k = " + k+"\n"+"            T[m]    X[m]    Nc[m] "+"\n";
						
						for (int m = 0; m < M; m++) {
							System.out.println(" m = " + (m + 1) + ":     " + fmat.format(T[m]) + "   " + fmat.format(X[m])
									+ "    " + fmat.format(Nc[m]));
							Ks=Ks+" m = " + (m + 1) + ":     " + fmat.format(T[m]) + "   " + fmat.format(X[m])
							+ "    " + fmat.format(Nc[m])+"\n";
						}
						System.out.println(" ");
						Ks=Ks+"\n";
				}

				String chaine = "*** Vecteur Pi ***\n";
				for (int i = 0; i < M; i++) {
					chaine = chaine + "Pi[" + i + "]= " + V[i] + "\n";
				}
				Ks=chaine+Ks;
				//output.send(0, new StringToken(Ks));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static void main(String[] args) throws IOException {
		
		 /*double[][] matrice ={ { 1, 1, 0, 0, 0, 0, 0 }, { 0, -3, 1, 0, 2, 0, 0 }, { 0, 0, -3, 1, 0, 2, 0 },
				{ 0, 0, 0, -2, 0, 0, 2 }, { 2, 0, 0, 0, -3, 1, 0 }, { 0, 2, 0, 0, 0, -3, 1 },
				{ 0, 0, 2, 0, 0, 0, -2 } };
			//{ { 0, 0.5, 0, 0, 0.5}, { 0.5, 0, 0.5, 0, 0 }, { 0, 0.5, 0, 0.5, 0 }, { 0, 0, 0.5, 0, 0.5 }, { 0.5, 0, 0, 0.5, 0 }, };
		 //{ { 0, 1, 0, 0, 0}, { 0, 0, 1, 0, 0 }, { 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0 }, };
		
		
		
		  
		
		  int M = 4;                                   // Number of nodes
	      int N = 3;                                  // Population size
	      int [] typeServer = {2,1,1,3};                // Node types (1, 2 or 3)
	      int [] NbServer = {2,1,1,1};                // Number of servers per node 
	      double [] mu = {2,1.666667,1.25,1};              // Service rates for each node
	      double [] V  = GTH(matrice, matrice.length); //{1,0.5,0.5,1};      // Visit ratios for each node

	      double [] Nc = new double [M];              // Mean number of customers
	      double [] T = new double [M];              // Mean response times
	      double [] X = new double [M];              // Throughputs
    		*/
		String fichier = "C:/Users/Altec/Desktop/N/test.txt";;
		// remove the quotes
       //fichier = fichier.substring(1, fichier.length() - 1);
       File file = new File(fichier);
       
		    try {
		    if(file.exists()!=true)JOptionPane.showMessageDialog(null, "Fichier introuvable");
		    else{
        	BufferedReader br = new BufferedReader(new FileReader(file));
        
		
		int[] NC = null;
		double[][] P = null;/*{ { 1, 1, 0, 0, 0, 0, 0 }, { 0, -3, 1, 0, 2, 0, 0 }, { 0, 0, -3, 1, 0, 2, 0 },
				{ 0, 0, 0, -2, 0, 0, 2 }, { 2, 0, 0, 0, -3, 1, 0 }, { 0, 2, 0, 0, 0, -3, 1 },
				{ 0, 0, 2, 0, 0, 0, -2 } };*/
		double[] mu = null;
		int[] typeServer = null;
		int[] NbServer = null;

		String type = type_fichier(fichier);
		NC = Nombre_De_ServerEtClient(fichier, type);

		int M = NC[0];
		int N = NC[1];
		mu = service(fichier, type, M);
		
		P = Matrice_de_transition(fichier, type, M);
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < M; j++) {
				System.out.print(P[i][j]+" | ");
			}System.out.println();
		}
		typeServer = Type(fichier, type, M);
		NbServer = Nbserver(fichier, type, M);
		
		double[] Nc = new double[M]; // # moyene de client dans la
		double[] T = new double[M]; // temp moyene de reponse
		double[] X = new double[M]; // A(i)
		double[] V = GTH(P, M);				
		
		for(int i=0;i<M;i++){
			System.out.println("V["+i+"] = "+V[i]);
		}
		
	      int numLD = 0;                    
	      for (int i=0; i<M; i++) {
	          if (typeServer[i] == 2) {numLD++;}            // Determine number of LD nodes
	      }
	      double [][] pMarg = new double [N+1][numLD+1];
	      for (int m=0; m<=numLD; m++) {
	         pMarg[0][m] = 1;                             // Marginals for LD nodes
	      }



	      
//	               ***** Begin iterating over customer population  ***
	      for (int k=1; k<=N; k++){

//	       ***  MVA Step 1:  Compute mean response times  ***
	         int LDcount = 0;                                   // Indicator for LD nodes
	         for (int m=0 ; m < M; m++) {
	            if (typeServer[m] == 3) { T[m] = 1/mu[m]; }          // Infinite server node
	            if (typeServer[m] == 1) { T[m] = (Nc[m]+1)/mu[m]; }   // FCFS exponential, etc.
	            if (typeServer[m] == 2) {                            // Load-dependent node
	               T[m] = 0;                                    //   ... considered to be a
	               for (int j=1; j <=k; j++) {                  //   multiserver expon. node
	                   // In this implementation, alpha_i(j) is equal to min(m_i, j)
	                   double alpha = j; if (NbServer[m] < j){alpha = NbServer[m];}
	                   T[m] = T[m] + j/(mu[m] * alpha) * pMarg[j-1][LDcount];
	               }
	               LDcount++;
	            }
	         }     

//	       ***  MVA Step 2:   Compute throughputs
	         double bottom = 0;
	         for (int m=0; m<M; m++){
	             bottom = bottom + V[m]*T[m];
	         }
	         X[0] = k/bottom;                          // Overall throughput
	         for (int m=1; m<M; m++) {
	             X[m] = V[m]*X[0];                     // Throughput of node m
	         }

//	       *** MVA Step 3:   Compute mean number of customers  ***
	         for (int m=0; m< M; m++) {
	            Nc[m] = X[m]*T[m];
	         }

//	               Compute marginals for all load-dependent nodes
	         LDcount = 0;                              //  Indicator for LD nodes
	         for (int m=0; m<M; m++) {
	             if (typeServer[m] == 2) {
	                double B = 0;
	                for (int j=k; j >=1; j--) {
	                    // In this implementation, alpha_i(j) is equal to min(m_i, j)
	                    double alpha = j; if (NbServer[m] < j){alpha = NbServer[m];}
	                    pMarg[j][LDcount] = X[m]/(mu[m] * alpha) * pMarg[j-1][LDcount];
	                    B = B + pMarg[j][LDcount];
	                } 
	                pMarg[0][LDcount] = 1-B;
	                LDcount++;
	            }
	         }
	      
	   
//  Output results
	      NumberFormat fmat = NumberFormat.getNumberInstance();
	      fmat.setMaximumFractionDigits(6);
	      fmat.setMinimumFractionDigits(6);
	      System.out.println("     " );
	      System.out.println("  k = " + k);
	      
			for (int m = 0; m < M; m++) {
				System.out.println(" m = " + (m + 1) + ":     " + fmat.format(T[m]) + "   " + fmat.format(X[m])
						+ "    " + fmat.format(Nc[m]));
				
			}
			System.out.println(" ");
			
	      }
		    }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	      }
	}
