package ua.nure.baranov.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import jade.wrapper.StaleProxyException;
import ua.nure.baranov.User;
import ua.nure.baranov.agent.SearchAgent;
import ua.nure.baranov.db.DAOFactory;
import ua.nure.baranov.db.UserDAO;
import ua.nure.baranov.gui.util.Messages;
/**
 * The entry point for Swing version.
 * @author Yevhenii Baranov
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static final int FRAME_WIDTH = 500;
	private static final int FRAME_HEIGHT = 500;
	private JPanel contentPanel;
	private JPanel browsePanel;
	private JPanel addPanel;
	private UserDAO dao;
	private DetailsPanel detailsPanel;
	private JPanel editPanel;
	private JPanel searchPanel;
	private SearchAgent agent;

	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame(null);
		mainFrame.setVisible(true);
	}

	public MainFrame(SearchAgent agent) {
		super();
		this.agent = agent;
		dao = DAOFactory.getInstance().getUserDAO();
		addWindowListener(new MainFrameCloser(agent));
		initialize();
	}
	
	public UserDAO getDAO() {
		return dao;
	}

	private void initialize() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		this.setTitle(Messages.getString("MainFrame.appName")); //$NON-NLS-1$
		this.setContentPane(getContentPanel());

	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(new BorderLayout());
			contentPanel.add(getBrowsePanel(), BorderLayout.CENTER);
		}
		return contentPanel;
	}

	private JPanel getBrowsePanel() {
		if (browsePanel == null) {
			browsePanel = new BrowsePanel(this);
			browsePanel.setName("browsePanel"); //$NON-NLS-1$
		}
		((BrowsePanel) browsePanel).initTable();
		return browsePanel;
	}

	private void showPanel(JPanel panel) {
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setVisible(true);
		panel.repaint();
	}

	private JPanel getAddPanel() {
		if (addPanel == null) {
			addPanel = new AddPanel(this);
			addPanel.setName("addPanel");
		}
		return addPanel;
	}
	/**
	 * Hides all panels of the app.
	 */
	public void disablePanels() {
		((BorderLayout) getContentPanel().getLayout()).getLayoutComponent(BorderLayout.CENTER).setVisible(false);
	}
	
	public DetailsPanel getDetailsPanel() {
		if (detailsPanel == null) {
			detailsPanel = new DetailsPanel(this);
			detailsPanel.setName("detailsPanel");
		}
		return detailsPanel;
	}

	private JPanel getEditPanel() {
		if (editPanel == null) {
			editPanel = new EditPanel(this);
			editPanel.setName("editPanel");
		}
		return editPanel;
	}
	/**
	 * Shows panel for adding user
	 */
	public void showAddPanel() {
		disablePanels();
		showPanel(getAddPanel());
	}
	/**
	 * Shows panel with all details about user
	 * @param userToShow user, whose data will be shown
	 */
	public void showDetailsPanel(User userToShow, String panelType) {
		disablePanels();
		((DetailsPanel) getDetailsPanel()).setParent(panelType);
		((DetailsPanel) getDetailsPanel()).setUser(userToShow);
		showPanel(getDetailsPanel());
	}
	/**
	 * Shows panel for browsing all users
	 */
	public void showBrowsePanel() {
		disablePanels();
		showPanel(getBrowsePanel());
	}
	/**
	 * Shows panel for editing user
	 * @param userToShow user that will be edited
	 */
	public void showEditPanel(User userToShow) {
		disablePanels();
		((EditPanel) getEditPanel()).setUser(userToShow);
		showPanel(getEditPanel());
	}

	public void showSearchPanel() {
		disablePanels();
		showPanel(getSearchPanel());
	}

	public JPanel getSearchPanel() {
		if (searchPanel==null) {
			searchPanel = new SearchGui(agent,this);
		}
		return searchPanel;
	}

	private class MainFrameCloser extends WindowAdapter{
		private SearchAgent agent;

		public MainFrameCloser(SearchAgent agent) {
			this.agent = agent;
		}
		
		@Override
		public void windowClosing(WindowEvent e) {
			try {
				agent.getContainerController().kill();
			} catch (StaleProxyException e1) {
				e1.printStackTrace();
				System.exit(-1);
			}
		}
	}
}
