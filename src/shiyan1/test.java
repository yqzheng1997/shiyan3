package shiyan1;
import java.util.Scanner;
import java.io .FileInputStream;
import java.util.Random;
import java.io.File;

class graph{
	private static int N = 500;
	private int wordnum;//单词个数
	private int[] edgenum=new int[N];//边条数
	private String[] word=new String[N];//单词数组
	private int[][] edge=new int[N][N];//边的二维数组
	//D:/t.txt
	public graph() {}
	public graph(String filepath) 
	{//传入文件路径，生成有向图
		try {
			FileInputStream in= new FileInputStream(filepath);
			int size=in.available();
			int prei=0;
			byte[] buffer = new byte[size];
			int i=0,j=0;
			int flag=-1;
			String[] wordt=new String[N];
			in.read(buffer);
			in.close();
			String str=new String(buffer,"GB2312");
			str=str.toLowerCase();
			
			for(i=0;i<127;i++)
			{
				if(i<65 || i>122 || (i>90 && i<97))
					str=str.replace((char)i,' ');
			}
		//	System.out.println(str);
			wordt=str.split("\\s+");
			for(i=0;i<wordt.length;i++) {
				flag=-1;
				for(j=0;j<i;j++) {
					if(wordt[i].equals(word[j])) {
						flag=j;
						break;
					}
				}
				if(flag==-1){
					if(i!=0) {
						
						if(edge[prei][wordnum]==0) {
							edgenum[prei]++;
						}
						edge[prei][wordnum]++;
					}
					prei=wordnum;
					word[wordnum]=wordt[i];
					wordnum++;
				}
				else {
					if(edge[prei][flag]==0) {
						edgenum[prei]++;
					}
					edge[prei][flag]++;
					prei=flag;
				}
			}
			for(i=0;i<wordnum;i++) {
				for(j=0;j<wordnum;j++) {
					if(edge[i][j]==0) {
						edge[i][j]=N;
					}
				}
			}
		}catch(Exception e) {
			System.out.println("读取文件失败！");
		}
	}
	public void outword() 
	{//输出单词及有向图的邻接矩阵
		int i=0;
		int j=0;
		System.out.print(wordnum+"个单词：\n");
		for(i=0;i<wordnum;i++)
		{
			System.out.println(word[i]+"("+edgenum[i]+")");
		}
		for(i=0;i<wordnum;i++) {
			for(j=0;j<wordnum;j++) {
				if(edge[i][j]<N) {
					System.out.println(word[i]+"->"+word[j]);
				}
				
			}
		}
	}
	public String queryBridgeWords(String word1,String word2) 
	{
		int i,j;
		int a=-1;
		int b=-1;
		int count=0;
		int flag1 = -1;
		int flag = -1;
		String str="";
		String bridge[] = new String[N];
		for(i=0;i<wordnum;i++) {
			if(word[i].equals(word1)) {
				a = i;
				flag = 1;
			}
			if(word[i].equals(word2)) {
				b = i;
				flag1 = 1;
			}
		}
		if (flag == -1 || flag1 == -1) {
			return null;
		}
		for(i=0;i<=wordnum;i++) {
			flag=flag1=-1;
			if(i!=a && i!=b) {
				for(j=0;j<edgenum[a];j++) {
					if(edge[a][i]<N) {
						flag = 1;
					}
				}
				for(j=1;j<=edgenum[i];j++) {
					if(edge[i][b]<N) {
						flag1 = 1;
					}
				} 
				if(flag==1 && flag1==1) {
					bridge[count]=word[i];
					count++;
				}
			}
		}
		if(count==0) {
			return " ";
		}
		else {
			for(i=0;i<count;i++) {
				str=str+bridge[i]+" ";
			}
		}
		return str;
	}
	public String generateNewText(String input)
	{
		String temp;
		String[] bri=new String[N];
		int start=0;
		int i;
		String[] word1=new String[N],word2=new String[N],ttemp=new String[N];
		int tcount=0;
		String str="";
		String newtext[] = new String[N];
		newtext=input.split(" ");
		for(i=0;i<newtext.length -1;i++) {
			temp=this.queryBridgeWords(newtext[i], newtext[i+1]);
			
			if(temp != null && temp.equals(" ")==false) {
				
				bri=temp.split(" ");
				temp=bri[0];
				word1[tcount]=newtext[i];
				word2[tcount]=newtext[i+1];
				ttemp[tcount]=temp;
				tcount++;
				
				for(int j=start;j<=i;j++) {
					str=str+newtext[j]+" ";
				}
				str=str+temp+" ";
				start = i+1;
			}
		}		
		this.picturebridge(word1,word2,ttemp,tcount);
		for(i=start;i<newtext.length;i++) {
			str=str+newtext[i]+" ";
		}
		return str;
	}
	public void picturebridge(String[] word1,String[] word2,String[] temp,int tcount) {
		int[][] flagedge=new int[N][N];
		int ei=0,ej=0,i=0,j=0,ti=0,k;
		for(k=0;k<tcount;k++) {
			for(j=0;j<wordnum;j++) {
				if(temp[k].equals(word[j])) {
					ti=j;
				}
				if(word1[k].equals(word[j])) {
					ei=j;
				}
				if(word2[k].equals(word[j])) {
					ej=j;
				}
			}
			flagedge[ei][ti]=1;
			flagedge[ti][ej]=1;
		}
		
		
		Graphviz gv = new Graphviz();
		gv.addln(gv.start_graph());
		
		for(i=0;i<wordnum;i++)
		{
			for(j=0;j<wordnum;j++)
			{
				if(edge[i][j]<N)
				{
					if(flagedge[i][j]==0) {
						gv.addln(word[i]+"->"+word[j]+"[label="+edge[i][j]+"]");
					}
					else {
						gv.addln(word[i]+"[color=red]");
						gv.addln(word[j]+"[color=red]");
						gv.addln(word[ti]+"[color=red]");
						gv.addln(word[i]+"->"+word[j]+"[color=orange,label="+edge[i][j]+"]");
						//System.out.println("哈哈哈");
					}
				}
			}
		}
		gv.addln(gv.end_graph());
	//	System.out.println(gv.getDotSource());
		gv.increaseDpi();   // 106 dpi
		String type = "png";
		String repesentationType= "dot";
		File out = new File("D:/lab1test/graphbridge." + type);    // Windows
		gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, repesentationType), out );
	}
	public String calcShortestPath(String word1,String word2) 
	{
		int i,j,k,a=-1,b=-1;
		int flag=-1,flag1=-1;
		int path[][]=new int[N][N];
		int dis[][]=new int[N][N];
		String str="";
		for(i=0;i<wordnum;i++) {
			if(word[i].equals(word1)) {
				a = i;
				flag = 1;
			}
			if(word[i].equals(word2)) {
				b = i;
				flag1 = 1;
			}
		}
		if (flag == -1 || flag1 == -1) {
			System.out.println("No "+ word1 +" or "+ word2 +" in this graph");
			return null;
		}
		for(i=0;i<wordnum;i++) {
			for(j=0;j<wordnum;j++) {
				path[i][j]=j;
				dis[i][j]=edge[i][j];
			}			
		}
		for(k=0;k<wordnum;k++) {
			for(i=0;i<wordnum;i++) {
				for(j=0;j<wordnum;j++) {
					if(dis[i][k]+dis[k][j]<dis[i][j]) {
						dis[i][j]=dis[i][k]+dis[k][j];
						path[i][j]=path[i][k];
					}
				}
			}
		}
		if(dis[a][b]<N) {
			System.out.println(word1+" 到 "+word2+" 的最短路径长度为："+dis[a][b]);
			str=this.picture_new(path, a, b);
			return str;
		}
		else {
			System.out.println(word1+" 到 "+word2+" 没有路");
			return null;
		}
	}
	public String randomWalk()
	{
		String str = "";
		int[] strword = new int[N];
		Random rand = new Random();
		int i,j,k,prei;
		int count=0;
		int countword=0;
		int flag=1;
		i=rand.nextInt(wordnum);
		prei=i;
		while(edgenum[i]!=0 && flag==1)
		{
			for(k=1;k<countword;k++)
			{
				if(strword[k]==i && prei==strword[k-1] && countword!=0)
				{
					flag=0;
					break;
				}
			}
			strword[countword]=i;
			countword++;
			if(flag==1)
			{
				j=rand.nextInt(edgenum[i]);
				count=0;
				for(k=0;k<wordnum;k++)
				{
					if(edge[i][k]<N)
					{
						count++;
						if(count-1==j)
							break;
					}
				}
				if(edge[i][k]<N) {
					prei=i;
					i=k;
				}
			}
		}
		for(j=0;j<countword;j++)
		{
			str=str+word[strword[j]]+" ";
		}
		
		if(edgenum[i]==0)
		{
			str=str+word[i];
		}
		return str;
	}
	public String picture_new(int[][] path,int a,int b)
	{
		int i,j;
		String str="";
		int[][] flag=new int[N][N];
		Graphviz gv = new Graphviz();
		gv.addln(gv.start_graph());
		i=a;
		str=word[a];
		while(i!=b) {
		//	gv.addln(word[j]+"->"+word[path[i][b]]+"[color=blue,label="+edge[j][path[i][b]]+"]");
			str=str+word[path[i][b]];
			flag[i][path[i][b]]=1;
			i=path[i][b];
		}
		for(i=0;i<wordnum;i++)
		{
			for(j=0;j<wordnum;j++)
			{
				if(edge[i][j]<N)
				{
					if(flag[i][j]==0) {
						gv.addln(word[i]+"->"+word[j]+"[label="+edge[i][j]+"]");
					}
					else {
						gv.addln(word[i]+"->"+word[j]+"[color=blue,label="+edge[i][j]+"]");
						
					}
				}
			}
		}
		gv.addln(gv.end_graph());
		//System.out.println(gv.getDotSource());
		gv.increaseDpi();   // 106 dpi
		String type = "png";
		String repesentationType= "dot";
		File out = new File("D:/lab1test/graphnew." + type);    // Windows
		gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, repesentationType), out );
		return str;
	}
	public void picture()
	{
		int i,j;		
		Graphviz gv = new Graphviz();
		gv.addln(gv.start_graph());
		for(i=0;i<wordnum;i++)
		{
			for(j=0;j<wordnum;j++)
			{
				if(edge[i][j]<N)
				{
					gv.addln(word[i]+"->"+word[j]+"[label="+edge[i][j]+"]");
				}
			}
		}
		gv.addln(gv.end_graph());
	//	System.out.println(gv.getDotSource());
		gv.increaseDpi();   // 106 dpi
		String type = "png";
		String repesentationType= "dot";
		File out = new File("D:/lab1test/graph." + type);    // Windows
		gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, repesentationType), out );
	}

}
public class test {

	public static void main(String[] args){
		 //TODO Auto-generated method stub
		Scanner scan = new Scanner(System.in );
		System.out.println("请输入要读取的文件路径");
		String filepath,word1,word2,input;
		String str;
		int k;
		filepath = scan.nextLine();
		
		//cd D:\java-learn\shiyan1\src
		//javac ./shiyan1/test.java
		//java shiyan1.test
		//D:/lab1test/lab1test.txt
		//of wild but surely than
		//pathos to than in western
		//the of the earth for coming never to mean
		
		
		//功能1建立
		graph ggg = new graph(filepath);
		//功能2展示
		ggg.outword();
		ggg.picture();
		//功能3桥接词
		
		for(k=0;k<3;k++) {
			System.out.println("请输入两个单词以生成桥接词");
			word1 = scan.nextLine();
			word2 = scan.nextLine();
			str=ggg.queryBridgeWords(word1,word2);
			if(str==null) {
				System.out.println("No "+ word1 +" or "+ word2 +" in this graph");
			}
			else if(str.equals(" ")) {
				System.out.println("No bridge words from "+word1+" to "+word2);
			}
			else {
				System.out.println("The bridge words from "+word1+" to "+word2+" are :"+str  );
			}
		}
		
			
		//功能4形成新句子
		for(k=0;k<3;k++) {
			System.out.println("请输入新的一句话");
			input = scan.nextLine();
			System.out.println("新生成的文本是：");
			str=ggg.generateNewText(input);
			System.out.println(str);
		}
		
		//功能5最短路径
		for(k=0;k<3;k++) {
			System.out.println("请输入两个单词以生成最短路径");
			word1 = scan.nextLine();
			word2 = scan.nextLine();
			ggg.calcShortestPath(word1, word2);
		}
		
		//功能6随机游走
		System.out.print("按回车键重新进行随机游走，按q退出");
		String go=scan.nextLine();
		while(go.equals("q")==false) {
			str=ggg.randomWalk();
			System.out.print(str);
			go=scan.nextLine();
		}
	
		scan.close();
	}

}