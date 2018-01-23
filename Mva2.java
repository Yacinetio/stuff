package org;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.kepler.metadata.ParserInterface;

import java_cup.parse_action;
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
import java.util.Vector;

public class Mva2 extends LimitedFiringSource {
	public Parameter username;
	public Parameter outt;
	public Parameter attrib;
	public TypedIOPort input;
	public TypedIOPort outNc;
	public TypedIOPort outK;
	public PortParameter arrayLength;

	public Mva2(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		_firingCountLimit = 1;

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

	// Fonction qui retourne le type d'un fichier
	public static String type_fichier(String fichier) {
		fichier = fichier.substring(0, fichier.length());
		int pos = fichier.lastIndexOf(".");
		if (pos > -1) {
			return fichier.substring(pos);

		} else {
			return fichier;
		}

	}

	// fonction qui lit le nombre de server et le nombre de client
	public static int[] Nombre_De_ServerEtClient(String fichier, String type) throws IOException {

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

	// fonction qui lit la matrice de transition
	public static double[][] Matrice_de_transition(String file, String type, int M) throws IOException {

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

	// Fonction qui lis le service ds servers
	public static double[] service(String file, String type, int M) throws IOException {
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
				System.out.println("S " + S);
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
				double[][] P = null;
				/*
				 * { { 1, 1, 0, 0, 0, 0, 0 }, { 0, -3, 1, 0, 2, 0, 0 }, { 0, 0, -3, 1, 0, 2, 0
				 * }, { 0, 0, 0, -2, 0, 0, 2 }, { 2, 0, 0, 0, -3, 1, 0 }, { 0, 2, 0, 0, 0, -3, 1
				 * }, { 0, 0, 2, 0, 0, 0, -2 } }
				 */;
				// double[] mu = null;
				int[] typeServer = null;
				int[] NbServer = null;

				String type = type_fichier(fichier);
				NC = Nombre_De_ServerEtClient(fichier, type);

				int M = 3;
				int[] N = { 2, 3 };
				int y = 0;
				int nMax = 0;
				for (int c = 0; c < N.length; c++) {
					if (nMax < N[c])
						nMax = N[c];
				}
				// mu = service(fichier, type, M);
				P = Matrice_de_transition(fichier, type, M);
				for (int i = 0; i < N.length; i++)
					y = y + N[i];
				typeServer = Type(fichier, type, M);
				NbServer = Nbserver(fichier, type, M);

				double[][] Nc = new double[M][N.length]; // # moyene de client dans la
				double[][] T = new double[M][N.length]; // temp moyene de reponse
				double[][] X = new double[M][N.length]; // A(i)
				// double[] V = GTH(P, M);
				double[][] mu = { { 5, 5 }, { 2.5, 1.6666667 }, { 1, 0.5 }, };
				double[][] V = { { 1, 1 }, { 0.6, 0.3 }, { 0.4, 0.7 }, };

				NumberFormat fmat = NumberFormat.getNumberInstance();
				fmat.setMaximumFractionDigits(3);
				fmat.setMinimumFractionDigits(3);

				int numLD = 0;
				for (int i = 1; i <= M; i++) {
					if (typeServer[i] == 2) {
						numLD++;
					} // Determine number of LD nodes
					NC[i] = 0;
				}
				double[][][] pMarg = new double[N.length][nMax][numLD + 1];
				for (int c = 0; c < N.length; c++) {
					for (int m = 0; m <= numLD; m++) {
						pMarg[c][0][m] = 1; // Marginals for LD nodes
					}
				}

				// int width = output.getWidth();
				String Ks = "";
				double[][] Tvalue = new double[y][M];

				for (int k = 1; k <= y; k++) {
					for (int i = 0; i <= k && i <= N[0]; i++) {
						for (int e = 0; e <= k - i && e <= N[1]; e++) {

							// *** MVA Step 1: Compute mean response times ***
							int LDcount = 0; // Indicator for LD nodes
							for (int c = 0; c < N.length; c++) {
								for (int m = 0; m < M; m++) {

									if (typeServer[m] == 3) {
										T[c][m] = 1 / mu[c][m];
									} // Infinite server node
									if (typeServer[m] == 1) {
										T[c][m] = (Nc[m][c] + 1) / mu[c][m];
									} // FIFO exponential, etc.
									if (typeServer[m] == 2) {
										T[c][m] = 0;
										for (int j = 1; j <= k; j++) {
											// In this implementation, alpha_i(j) is equal to min(m_i, j)
											double alpha = j;
											if (NbServer[m] < j) {
												alpha = NbServer[m];
											}
											T[c][m] = T[c][m] + j / (mu[c][m] * alpha) * pMarg[c][j - 1][LDcount];

										}
										LDcount++;
									}
									// Tvalue[k-1][m]=T[m];

								}
							}
							String attribut = outt.getExpression();
							System.out.println(attribut);
							output.send(0, new DoubleToken(Tvalue[k - 1][Integer.parseInt(attribut)]));
							double[][] Nvalue = new double[y][M];
							double[][] Xvalue = new double[y][M];
							// *** MVA Step 2: Compute throughputs
							double bottom = 0;
							for (int c = 0; c < N.length; c++) {
								for (int m = 0; m < M; m++) {
									bottom = bottom + V[c][m] * T[c][m];
								}

								X[c][0] = k / bottom; // Overall throughput
								for (int m = 1; m < M; m++) {
									X[c][m] = V[c][m] * X[c][0]; // Throughput of node m
									// Xvalue[k-1][m]= X[c][m];

								}

								// outK.send(0, new DoubleToken(k));

								// *** MVA Step 3: Compute mean number of customers ***

								for (int m = 0; m < M; m++) {
									Nc[c][m] = X[c][m] * T[c][m];
									// Nvalue[k-1][m]= Nc[c][m];
								} // outNc.send(0, new DoubleToken(Nvalue[k-1][Integer.parseInt(attribut)]));

								// Compute marginals for all load-dependent nodes
								LDcount = 0; // Indicator for LD nodes
								for (int m = 0; m < M; m++) {
									if (typeServer[m] == 2) {
										double B = 0;
										for (int j = k; j >= 1; j--) {
											// In this implementation, alpha_i(j) is equal to min(m_i, j)
											double alpha = j;
											if (NbServer[m] < j) {
												alpha = NbServer[m];
											}
											pMarg[c][j][LDcount] = X[c][m] / (mu[c][m] * alpha)
													* pMarg[c][j - 1][LDcount];
											B = B + pMarg[c][j][LDcount];
										}
										pMarg[c][0][LDcount] = 1 - B;
										LDcount++;
									}
								}

							}
							// Output results
							System.out.println("     ");
							System.out.println("  k = " + k);
							Ks = Ks + "  k = " + k + "\n" + "            T[m]    X[m]    Nc[m] " + "\n";

							for (int m = 0; m < M; m++) {
								System.out.println(" m = " + (m + 1) + ":     " + fmat.format(T[m]) + "   "
										+ fmat.format(X[m]) + "    " + fmat.format(Nc[m]));
								Ks = Ks + " m = " + (m + 1) + ":     " + fmat.format(T[m]) + "   " + fmat.format(X[m])
										+ "    " + fmat.format(Nc[m]) + "\n";
							}
							System.out.println(" ");
							Ks = Ks + "\n";
						}
					}
				}

				String chaine = "*** Vecteur Pi ***\n";
				for (int i = 0; i < M; i++) {
					chaine = chaine + "Pi[" + i + "]= " + V[i] + "\n";
				}
				Ks = chaine + Ks;
				// output.send(0, new StringToken(Ks));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	 public static int valeurK(int i, int r,int N, Vector<Vector<Integer>> vect) {
	    	boolean found = false;
			int v=0;
			int x=0;
			while ( x < i && found == false) {
				int cont=0;
				if (vect.elementAt(x).elementAt(r) == vect.elementAt(i).elementAt(r) - 1) {
					cont++;
					for (int h = 0; h < N; h++) {
						if (h != r && vect.elementAt(x).elementAt(h) == vect.elementAt(i)
								.elementAt(h)) {
						cont++;
						}
					}
					if(cont==N) {
						v=x;
						found=true;
						}
				} x++;
			}
			return v;
	    }

	
	public static void main(String[] args) throws IOException {

		int[] typeServer = { 2, 1, 3 };
		int[] NbServer = { 2, 1, 1 };

		int M = 3;
		int[] N = { 2, 1 };
		Vector<Vector<Integer>> vect = new Vector<Vector<Integer>>();
		NumberFormat fmat = NumberFormat.getNumberInstance();
		fmat.setMaximumFractionDigits(3);
		fmat.setMinimumFractionDigits(3);

		if (N.length == 2) {
			for (int e = 0; e <= N[0]; e++) {
				for (int i = 0; i <= N[1]; i++) {
					Vector<Integer> v = new Vector<Integer>();
					v.add(0, (int) e);
					v.add(1, (int) i);
					vect.add(v);
				}
			}
		}
		if (N.length == 3) {
			for (int e = 0; e <= N[0]; e++) {
				for (int i = 0; i <= N[1]; i++) {
					for (int y = 0; y <= N[2]; y++) {
						Vector<Integer> v = new Vector<Integer>();
						v.add(0, (int) e);
						v.add(1, (int) i);
						v.add(2, (int) y);
						vect.add(v);

					}
				}
			}
		}
		
		double[][][] Nc = new double[M][N.length][vect.size()]; // # moyene de client dans la
		double[][][] T = new double[M][N.length][vect.size()]; // temp moyene de reponse
		double[][] X = new double[N.length][vect.size()]; // A(i)
		// double[] V = GTH(P, M);
		double[][] mu = { { 5, 5 }, { 2.5, 1.666666666666667 }, { 1, 0.5 }, };
		double[][] V = { { 1, 1 }, { 0.6, 0.3 }, { 0.4, 0.7 }, };

		
		int nmax=0;
		for(int i=0;i<N.length;i++) {
			if(nmax<N[i]) nmax=N[i];
		}
		double[][][] pMarg = new double[M][nmax+1][vect.size()];
		
		pMarg[0][0][0] = 1; // Marginals for LD nodes
		
		
		for (int i = 0; i < M; i++) {
			for (int j = 1; j <= NbServer[i] - 1; j++) {
				pMarg[0][j][0] = 0;
			}
			for (int c = 0; c < N.length; c++) {
				Nc[i][c][0] = 0;
			}
		}
		

		// int width = output.getWidth();
		String Ks = "";
		// double[][] Tvalue= new double[y][M];

		for (int i = 0; i < vect.size(); i++) {

			// *** MVA Step 1: Compute mean response times ***

			for (int m = 0; m < M; m++) {
				for (int c = 0; c < N.length; c++) {
					double u=0;
					if (typeServer[m] == 3) {
						T[m][c][i] = 1 / mu[m][c];
					} // Infinite server node
					else if (typeServer[m] == 1) {
						
						int v =valeurK(i, c,N.length, vect);
						
						for(int s=0;s<N.length;s++) {
							u=u+Nc[m][s][v];
						}
							
						T[m][c][i] = (u+1) * 1/mu[m][c];
										
					} // FIFO exponential, etc.
					else if (typeServer[m] == 2) {
						
						double p = 0;
						double t = 0;
						int v =valeurK(i, c,N.length, vect);
						for (int s = 0; s < N.length; s++) {
						p = p + Nc[m][s][v];
						}	
						for (int j = 0; j <= NbServer[m] - 2; j++)
							t = t + (NbServer[m] - j - 1) * (pMarg[m][j][v]);
						T[m][c][i] = 1 / (mu[m][c] * NbServer[m]) * (1 + p + t);
					}
									
						// Tvalue[k-1][m]=T[m];

					

				}
			}

			// *** MVA Step 2: Compute throughputs

			for (int c = 0; c < N.length; c++) {
				double bottom = 0;
				for (int m = 0; m < M; m++) {
					bottom = bottom + V[m][c] * T[m][c][i];
				}
				X[c][i] = vect.elementAt(i).elementAt(c) / bottom; // Overall throughput

				// outK.send(0, new DoubleToken(k));

				// *** MVA Step 3: Compute mean number of customers ***

				for (int m = 0; m < M; m++) {
					Nc[m][c][i] = X[c][i] * T[m][c][i] * V[m][c];
					// Nvalue[k-1][m]= Nc[c][m];
				} // outNc.send(0, new DoubleToken(Nvalue[k-1][Integer.parseInt(attribut)]));

				// Compute marginals for all load-dependent nodes
			}
				for (int m = 0; m < M; m++) {
					if(typeServer[m]!=3) {
						
						for (int j = 1; j <= NbServer[m] - 1; j++) {
							double u = 0;
							int v =0;
							for (int r = 0; r < N.length; r++) {
								
								v =valeurK(i, r,N.length, vect);
								u = u + (X[r][i] * (V[m][r] / mu[m][r]) * pMarg[m][j - 1][v]);
								
							}
							pMarg[m][j][i] = (1 / j) * u;
							//System.out.println("pj "+pMarg[m][j][i]);
						}

						double n = 0;
						double B = 0;
						for (int r = 0; r < N.length; r++)
							{
							n = n + (X[r][i] * (V[m][r] / mu[m][r]));
							
							}

						for (int j = 1; j <= NbServer[m] - 1; j++) {
							B = B + ((NbServer[m] - j) * pMarg[m][j][i]);
							
						}
							double p = NbServer[m];
							pMarg[m][0][i] = 1 - (1 / p) * (n + B);
						//System.out.println("p0 "+pMarg[m][0][i]+" n = "+n+ " B= "+B);

					}
					
				}
			

			// Output results
			// System.out.println(" " );
			System.out.println("  e = " + vect.elementAt(i) + " i = " + i);

			for (int m = 0; m < M; m++) {
				for (int c = 0; c < N.length; c++) {

					System.out.println(" m = " + (m + 1) + ":     " + fmat.format(T[m][c][i]) + "   "
							+ fmat.format(X[c][i]) + "    " + fmat.format(Nc[m][c][i]));
					// Ks=Ks+" m = " + (m + 1) + ": " + fmat.format(T[c][m]) + " " +
					// fmat.format(X[c][m])
					// + " " + fmat.format(Nc[c][m])+"\n";
				}
			}
			System.out.println(" ");
			Ks = Ks + "\n";

		} //System.out.println("pmarg "+pMarg[0][0][3]);
		//System.out.println("***pour e = " + vect.elementAt(4) + " -> " + pMarg[0][0][5]);

	}
}
