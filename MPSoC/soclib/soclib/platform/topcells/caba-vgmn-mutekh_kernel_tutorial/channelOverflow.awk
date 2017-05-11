#!/usr/bin/gawk -f
#lancé avec "awk -f channelOverflow.awk fichier.log"
#écrit par Côme Demarigny

BEGIN{

#initialisation
    FS="[,_]"; #on reconnais les colonnes avec "," et "_" afin de pourvoir changer les "_" au moment de l'affichage
   
    outputFile1="results.txt";
    outputFile2="plottingResults.gnu";
    outputFile3="accesTime.txt";

    outputGraph="graph.pdf";

#-----------------limite pour l'affichage
    seuilOverflow=0;
    if (sizeFIFO!=NULL){
	seuilOverflow=sizeFIFO;
    }
#--------------


    i=1;
    transfert=0;
    interTransfert=0;
    somme=0;
    somme2=0;
    lookForInit=1;
    kmax=0;
    alreadySaved=0;
    channelAmount=0;
    maxInput=0;
    maxValue=0;
}


{ 
#mise en mémoire des colonnes en fonction du nom du canal
#les colonnes changes en fonction du canal car on detecte les "_" comme des séparateurs de colonnes
#le code suivant fait la différence entre les longeurs de noms.

#afin de racourcir les nom des canaux il pourrais être pertinent de rechercher l'indice "__" et de garder seulement le nom après ce marqueur

    if( ($5<=5) && ($6=="write"||$6=="read"||$6=="lock") ){
	timestamp=$3;
	channel=$4"-on-"$5;
	proc=$5;
	state=$6"_"$7;
	value=$8;
	channelID=4;
    }
    
    if($6<=5 && ($7=="write"||$7=="read"||$7=="lock")){
	timestamp=$3;
	channel=$4"-"$5"-on-"$6;
	proc=$6;
	state=$7"_"$8;
	value=$9;
	channelID=5;
    }
    
    if($7<=5 && ($8=="write"||$8=="read"||$8=="lock")){
	timestamp=$3;
	channel=$4"-"$5"-"$6"-on-"$7;
	proc=$7;
	state=$8"_"$9;
	value=$10;
	channelID=6;
    }
    
    if($8<=5 && ($9=="write"||$9=="read"||$9=="lock")){
	timestamp=$3;
	channel=$4"-"$5"-"$6"-"$7"-on-"$8;
	proc=$8;
	state=$9"_"$10;
	value=$11;
	channelID=7;
    }
    
    if($9<=5 && ($10=="write"||$10=="read"||$10=="lock")){
	timestamp=$3;
	channel=$4"-"$5"-"$6"-"$7"-"$8"-on-"$9;
	proc=$9;
	state=$10"_"$11;
	value=$12;
	channelID=8;
    }
    
    if($10<=5 && ($11=="write"||$11=="read"||$11=="lock")){
	timestamp=$3;
	channel=$4"-"$5"-"$6"-"$7"-"$8"-"$9"-on-"$10;
	proc=$10;
	state=$11"_"$12;
	value=$13;
	channelID=9;
    }
    
    if($11<=5 && ($12=="write"||$12=="read"||$12=="lock")){
	timestamp=$3;
	channel=$4"-"$5"-"$6"-"$7"-"$8"-"$9"-"$10"-on-"$11;
	proc=$11;
	state=$12"_"$13;
	value=$14;
	channelID=10;
    }

    if($12<=5 && ($13=="write"||$13=="read"||$13=="lock")){
	timestamp=$3;
	channel=$4"-"$5"-"$6"-"$7"-"$8"-"$9"-"$10"-"$11"-on-"$12;
	proc=$12;
	state=$13"_"$14;
	value=$15;
	channelID=11;
    }

    if($13<=5 && ($14=="write"||$14=="read"||$14=="lock")){
	timestamp=$3;
	channel=$4"-"$5"-"$6"-"$7"-"$8"-"$9"-"$10"-"$11"-"$12"-on-"$13;
	proc=$13;
	state=$14"_"$15;
	value=$16;
	channelID=12;
    }

    if($14<=5 && ($15=="write"||$15=="read"||$15=="lock")){
	timestamp=$3;
	channel=$4"-"$5"-"$6"-"$7"-"$8"-"$9"-"$10"-"$11"-"$12"-"$13"-on-"$14;
	proc=$14;
	state=$15"_"$16;
	value=$17;
	channelID=13;
    }

    if($15<=5 && ($16=="write"||$16=="read"||$16=="lock")){
	timestamp=$3;
	channel=$4"-"$5"-"$6"-"$7"-"$8"-"$9"-"$10"-"$11"-"$12"-"$13"-"$14"-on-"$15;
	proc=$15;
	state=$16"_"$17;
	value=$18;
	channelID=14;
    }

   # print "\t" lookForInit;
#recherche de fin d'initialisation
    if(lookForInit==1){
	if(state=="read_lock"){
	    endInit=timestamp;
	    lookForInit=0;
	}
    }
#debug
    # print state;
    # print state lookForInit;
    # print state "\t" lookForInit;
    # print state " \t" lookForInit;
    # print state "\t\t" lookForInit "\t" state;
    # print state "\t\t" lookForInit "\t" state "\t" somme;


#sauvegarde des temps de résolution
    if(lookForInit==0){                 #on ignore les inits
	if(state==("lock_release")&&Pstate!="lock-take"){ #on ignore les passages sans transferts
	    duration[transfert]=timestamp-Ptimestamp;          #écriture des temps de résolution
	    somme+=duration[transfert];
	    transfert++;
	}
	if(state==("read_lock")){
	    interDuration[interTransfert]=timestamp-Ptimestamp;
	    somme2+=interDuration[interTransfert]
	    interTransfert++;
	}
    }

#mise de tous les canaux dans un tableau

    j=0;
    if(channel!=Pchannel){        #test si canal different du transfert précédent
	while(channelName[j]!=NULL){ #test si case j vide
	    if(channelName[j]==channel){ #test si le canal actuel est déjà sauvé
		alreadySaved=1;
		break;
	    }
	    if(alreadySaved==0){
		j++;
	    }
	}
	if(channelName[j]==NULL){     #vérifie la raison de sortie de la boucle
	    channelName[j]=channel;   #sauvegarde le canal
	    channelAmount++;
	}
	currentChannelNumber=j;
    }
    alreadySaved=0;     #remise à 0 pour passage suivant
    
#enregistrement des valeurs de tous les canaux
    
    if(state=="write_usage"){
	k=0;
	j=currentChannelNumber;
	while(1){
	    if(channelName[j,k]==NULL){
		channelName[j,k]=value;
		if (value==0){
		    channelName[j,k]=0;
		}
		l=0;
		while(timeTracker[currentChannelNumber,l]!=NULL){
		    l++;
		}
		timeTracker[currentChannelNumber,l]=timestamp;
		maxInput++;
		break;
	    }
	    k++;
	}
    }
    
    
    
#test de remplissage des canaux
    
    if (state=="write_usage"&&channelName[j,k-1]>=seuilOverflow){      #<---- comportement par rapport au seuilOverflow  changer > par >= si besoin
	j=0;
	while((j<i)&&channelOverflow[j]!=channel){
	    j++;
	}
	if(channelOverflow[j]!=channel){
	    channelOverflow[i-1]=channel;
	    i++;
	}
    }
    
#recherche du max des canaux en overflow
    
    for(j=0;j<i-1;j++){
    	if(channel==channelOverflow[j]&&state=="write_usage"){     #on verifie si le canal est en overflow et on cherche write_usage
    	    k=0
	    if(channelOverflow[j,0,0]==NULL){
		channelOverflow[j,0,0]=0;
	    }
	    if(value>channelOverflow[j,0,0]){
		channelOverflow[j,0,0]=value;
		break;
	    }
	    k++;
    	}
    }
#sauvegarde de la plus grande valeur
    if(state=="write_usage"){
	if(value>maxValue){
	    maxValue=value;
	}
    }

#sauvegarde de l'etat précédent

    Ptimestamp=timestamp;
    Pchannel=channel;
    Pproc=proc;
    Pstate=state;
    Pvalue=value;

}




END{
#Moyenne de temps de résolution
    if(transfert!=0){
	moyenne=somme/transfert;
	moyenne2=somme2/interTransfert;
	print "temps de résolution moyen: " moyenne " cycles\ntemps entre transferts moyen: " moyenne2 " cycles\n\ntemps de transfert: \t \ttemps entre transferts:" > outputFile3 ;
	for (j=0;j<=transfert+1;j++){
	    print duration[j]"\t \t \t \t" interDuration[j] > outputFile3 ;
	}
    } else {
	print "aucun transfert effectué ou log corrompu" > outputFile3 ;
    }
    
#Overflow

  # print "canaux dépassant le seuil d'overflow :"
  # for(j=0;j<i-1;j++){
  # 	print channelOverflow[j]
  # 	print channelOverflow[j,0,max]
  # }

#Noms des canaux et leurs remplissage si overflow

 # for(j=0;channelName[j]!=NULL;j++){
 #     for(jj=0;jj<i-1;jj++){
 # 	 if(channelName[j]==channelOverflow[jj]){
 # 	     print channelName[j];
 # 	     for(k=0;channelName[j,k]!=NULL;k++){
 # 		 print channelName[j,k];
 # 	     }
 # 	 }
 #     }
 # }

#Noms des canaux et leurs remplissage

 # for(j=0;channelName[j]!=NULL;j++){
 #     print channelName[j];
 #     for(k=0;channelName[j,k]!=NULL;k++){
 # 	 print channelName[j,k];
 #     }
 # }

#sauvegarde des données dans un nouveau tableau uniquement pour les canaux en overflow

for(j=0;channelName[j]!=NULL;j++){
     for(jj=0;jj<i-1;jj++){
 	 if(channelName[j]==channelOverflow[jj]){
	     dataName[j]=channelName[j];
 	     for(k=0;channelName[j,k]!=NULL;k++){
		 dataName[j,k]=channelName[j,k];
		 dataTime[j,k]=timeTracker[j,k]
 	     }
 	 }
     }
 }

#replacement des valeurs en début de tableau dataName

 overflowAmount=0;
 for(i=0;i<=channelAmount;i++){
     for(j=i+1;j<=channelAmount;j++){     
	 if(dataName[i,0]==NULL){
	     if(dataName[j,0]!=NULL){
		 for(k=0;k<=maxInput;k++){
		     dataName[i]=dataName[j];
		     dataName[i,k]=dataName[j,k];
		     dataTime[i,k]=dataTime[j,k];
		     dataName[j,k]=NULL;
		     timeTracker[j,k]=NULL;
		 }
		 overflowAmount++;
	     }
	 }
     }
 }
#écriture des résultats selon le nombre d'overflow dans outputFile1

 for(k=0;k<=maxInput;k++){
     for(j=0;j<=overflowAmount;j++){
	 if(dataName[j,k]!=NULL){
	     printf dataTime[j,k]"\t"  > outputFile1;
	     for(i=0;i<j;i++){
		 printf ".""\t" > outputFile1;
	     }
	     printf dataName[j,k] > outputFile1;
	     for(i=j+1;i<overflowAmount;i++){
		 printf "\t""." > outputFile1;
	     }
	     printf  "\n" > outputFile1;
	 }
     }
 }

#génération du script gnuplot
#commande: "gnuplot plottingResults.gnu"

 print "set terminal pdf" > outputFile2;
 print "set output \""outputGraph"\"" > outputFile2;
 print "set title 'Overflow of "FILENAME"'" > outputFile2;
 print "set xlabel 'timestamp'" > outputFile2;
 print "set ylabel 'value'" > outputFile2;
 print "set yrange [0:"(maxValue+4)"]" > outputFile2;
 print "set xrange [0:"timestamp"]" > outputFile2;
 print "set grid" > outputFile2;
 print "set datafile separator '\\t'" > outputFile2;
 print "set datafile missing '.'" > outputFile2;
 print "plot \\"> outputFile2;
 for(j=0;j<overflowAmount;j++){
     printf "'"outputFile1"' u 1:"(j+2)" with linespoint title '"dataName[j]"'" > outputFile2;
     if(j<overflowAmount-1){
     	 printf "\\\n, " > outputFile2 ;
     }
 }

#commande d'ouverture du graph généré en pdf
#ne fonctionne que sur linux (commande acroread) pour compatibilité windows et mac il faudrait changer la commande suivante

 print "\nsystem(\"acroread "outputGraph" &\")" > outputFile2

#lancement du script gnuplot

 close(outputFile2);
 system("gnuplot "outputFile2);

#fin

}