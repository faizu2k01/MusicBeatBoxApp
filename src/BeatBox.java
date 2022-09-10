import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.util.*;
import java.io.*;

public class BeatBox {

	
	JPanel mainPanel;
	ArrayList<JCheckBox> checkBoxList;
	Sequencer sequencer;
	Sequence sequence;
	Track track;
	JFrame frame;
	JButton start,stop,upTempo,downTempo,storestate,restorestate;
	String[] instrumentName = {"","Bass Drum"," ","Closed Hi-Hat"," ","Open Hi-Hat"," ","Acoustic Snare"," ","Crash Cymbak"," ","Hand Clap"," ","High Tom"," ","Hi Bongo"," ","Maracas"," ","Whistle"," ","Low Congo"," ","Cowbell"," ","Vibraslap"," ","Low-mid Tom"," ","High Agogo"," ","Open Hi Conga"};
	
	int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
	
		public static void main(String[] arg)
	{
		BeatBox box = new BeatBox();
		box.work();
		
	}
	public void work()
	{
		frame = new JFrame("MidiMusicBeatBox");
		frame.setVisible(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout  = new BorderLayout();
		JPanel Background = new JPanel(layout);
		Background.setVisible(true);
		Background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		
        checkBoxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        
        
		start = new JButton("Start");
		start.setVisible(true);
		start.addActionListener(new MyStartListener());
	    buttonBox.add(start);
	    
	    stop = new JButton("Stop");
	    stop.setVisible(true);
	    stop.addActionListener(new MyStopListener());
	    buttonBox.add(stop);
	    
	    upTempo = new JButton("Tempo Up");
	    upTempo.setVisible(true);
	    upTempo.addActionListener(new MyUpTempoListener());
	    buttonBox.add(upTempo);
	    
	    downTempo = new JButton("Tempo Down");
	    downTempo.setVisible(true);
	    downTempo.addActionListener(new MyDownTempoListener());
	    buttonBox.add(downTempo);
	    
	    storestate = new JButton("Save");
	    storestate.setVisible(true);
	    storestate.addActionListener(new StoreState());
	    buttonBox.add(storestate);
	    
	    restorestate = new JButton("Open saved");
	    restorestate.setVisible(true);
	    restorestate.addActionListener(new OpenState());
	    buttonBox.add(restorestate);
	    
	    Box nameBox = new Box(BoxLayout.Y_AXIS);
	    for(int i = 0;i<32;i++)
	    {
	    	nameBox.add(new JLabel(instrumentName[i]));
	    	
	    	
	    	
	    }
	    
	    Background.add(BorderLayout.EAST,buttonBox);
	    Background.add(BorderLayout.WEST,nameBox);
	    
	    
	    GridLayout grid = new GridLayout(16,16);
	    grid.setVgap(2);
	    grid.setHgap(1);
	    mainPanel = new JPanel(grid);
	    mainPanel.setVisible(true);
	    Background.add(BorderLayout.CENTER,mainPanel);
	    frame.getContentPane().add(Background);
	    
	    for(int i=0;i<256;i++)
	    {
	    	JCheckBox c = new JCheckBox();
	    
	    	c.setSelected(false);
	    	checkBoxList.add(c);
	    	mainPanel.add(c);	    
	    }	
	    setUpmidi();
	    frame.setBounds(80,50,500,800);
	   frame.pack();
	    
	    }
	public void setUpmidi()
	{
		try {
			 sequencer = MidiSystem.getSequencer();
			 sequencer.open();
			 sequence  = new Sequence(Sequence.PPQ,4);
			 track = sequence.createTrack();
			 sequencer.setTempoInBPM(120);
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
		public void buildTrackAndStart()
		{
			int[] trackList = null;
			sequence.deleteTrack(track);
			track=  sequence.createTrack();
			
			for(int i = 0;i<16;i++)
			{
				trackList = new int[16];
				
				int key = instruments[i];
				
				for(int j = 0;j<16;j++)
				{
					JCheckBox jc = checkBoxList.get(j+16*i);
					if(jc.isSelected())
					{
						trackList[j]=key;
						
					}
					else
					{
						trackList[j]=0;
			
					}
				}
				makeTracks(trackList);
				track.add(makeEvent(176,1,127,0,16));
				
			}
			track.add(makeEvent(176,1,127,0,16));
			try
			{
				sequencer.setSequence(sequence);
				sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
				sequencer.start();
				sequencer.setTempoInBPM(120);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
		}
	
	
class MyStartListener implements ActionListener
{
	public void actionPerformed(ActionEvent event1)
	{
		buildTrackAndStart();
	}
}

class MyStopListener implements ActionListener
{
	public void actionPerformed(ActionEvent event1)
	{
		sequencer.stop();
	}
}

class MyUpTempoListener implements ActionListener
{
	public void actionPerformed(ActionEvent event1)
	{
		float tempoFactor = sequencer.getTempoFactor();
		sequencer.setTempoFactor((float)(tempoFactor*1.03));
		
	}
}

class MyDownTempoListener implements ActionListener
{
	public void actionPerformed(ActionEvent event1)
	{
		float tempoFactor = sequencer.getTempoFactor();
		sequencer.setTempoFactor((float)(tempoFactor*.97));
		
	}
}

public void makeTracks(int[] list)
{
	for(int i =0;i<16;i++)
	{
		int key = list[i];
		
		if(key!=0)
		{
			track.add(makeEvent(144,9,key,100,i));
			track.add(makeEvent(128,9,key,100,i+1));

		}
	}
}
public MidiEvent makeEvent(int comd,int chn,int one,int two,int tick)
{
	MidiEvent event = null;
	try
	{
		ShortMessage a = new ShortMessage();
		a.setMessage(comd,chn,one,two);
		event = new MidiEvent(a,tick);
		
	}
	catch(Exception a)
	{
		a.printStackTrace();
	}
	return event;
}
public class StoreState implements ActionListener
{
	public void actionPerformed(ActionEvent e4)
	{
		boolean[] checkboxState = new boolean[256];
		for(int i =0; i<256;i++)
		{
			JCheckBox check = (JCheckBox) checkBoxList.get(i);
			if(check.isSelected())
			{
				checkboxState[i] =true;
			}
			
		}
		
		try {
			FileOutputStream fOut = new FileOutputStream(new File("Checkbox.ser"));
			ObjectOutputStream os = new ObjectOutputStream(fOut);
			os.writeObject(checkboxState);
		}
		catch(Exception w)
		{
			w.printStackTrace();
		}
	}
}

public class OpenState implements ActionListener
{
	public void actionPerformed(ActionEvent e5)
	{
		boolean[] checkboxState = null;
		try {
			FileInputStream fIN  = new FileInputStream("Checkbox.ser");
			ObjectInputStream oIn = new ObjectInputStream(fIN);
			checkboxState = (boolean[]) oIn.readObject();
			oIn.close();
		}
		catch(Exception w8)
		{
			w8.printStackTrace();
		}
		
		for(int i =0;i<256;i++)
		{
			JCheckBox check = (JCheckBox) checkBoxList.get(i);
			if(checkboxState[i])
			{
				check.setSelected(true);
			}
			else
			{
				check.setSelected(false);
			}
		}
		sequencer.stop();
		buildTrackAndStart();
	}
}
}

