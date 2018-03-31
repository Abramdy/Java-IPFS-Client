package fr.rhaz.ipfs.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import javax.swing.JTree;
import java.awt.BorderLayout;
import javax.swing.JTextPane;
import javax.swing.JList;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import net.miginfocom.swing.MigLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JTable;
import javax.swing.JSplitPane;

@Deprecated
public class Explorer extends JFrame {

	private static final long serialVersionUID = -3527557178627329029L;
	private List<MerkleNode> nodes;
	private JTable table;
	
	public Explorer(List<MerkleNode> nodes) {
		setResizable(false);
		this.nodes = nodes;
		
		this.setSize(600, 400);
		
		String[] header = {"Name", "Size", "Type"};
		
		List<Object[]> dataslist = new ArrayList<>();
		
		for(MerkleNode node:nodes) {
			try{
				Object[] data = {
					node.name.orElse(node.hash.toBase58()), 
					node.size.orElseThrow(() -> new Exception()),
					NodeType.values()[node.type.orElseThrow(() -> new Exception())]
				};
				dataslist.add(data);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		Object[][] datas = dataslist.toArray(new Object[dataslist.size()][]);
		
		setTitle("IPFS Explorer");
		this.setIconImage(Window.logo.getImage());
		getContentPane().setLayout(null);
		
		JTree tree = new JTree();
		tree.setBounds(0, 0, 119, 353);
		getContentPane().add(tree);
        
        JPanel panel = new JPanel();
        panel.setBounds(131, 0, 451, 353);
        getContentPane().add(panel);
        panel.setLayout(new BorderLayout(0, 0));
        
        table = new JTable(datas, header);
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(table, BorderLayout.CENTER);
		
        
		this.setVisible(true);
	}
}
