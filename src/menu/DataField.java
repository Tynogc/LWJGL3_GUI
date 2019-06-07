package menu;

import javax.security.auth.Destroyable;

import utility.BindText;
import utility.InputEvent;
import utility.BindText.BINDING;
import gui.PicLoader;
import main.graphics.util.Color;
import gui.SpriteBatch;
import main.graphics.TextureRegion;

public class DataField extends AbstractButton implements Destroyable{

	private TextureRegion[][] tr;
	
	public Color backColor = Color.WHITE;
	public Color textColor = Color.BLACK;
	
	private FontRenderer font;
	
	private boolean canTextEnter = false;
	private boolean isActive;
	
	private String overlayText;
	
	private AdvancedTextEnterField adf;
	private String lastADF = "";
	private int lastXsizeTO = 0;
	
	private BindText.BINDING textBinding = BINDING.BIND_LEFT;
	private int textOffset;
	
	private boolean isPassword;
	
	private ToolTip tt;
	
	public boolean ignoreNextRelease;
	public boolean changeTextByFetch = true;
	
	public DataField(int x, int y, int xSize) {
		super(x, y);
		TextureRegion t = PicLoader.pic.getImage("DataField1");
		tr = new TextureRegion[2][3];
		tr[0][0] = new TextureRegion(t, 0, 0, 5, t.getHeight());
		tr[0][1] = new TextureRegion(t, 5, 0, 11, t.getHeight());
		tr[0][2] = new TextureRegion(t, 16, 0, 5, t.getHeight());
		
		t = PicLoader.pic.getImage("DataField2");
		tr[1][0] = new TextureRegion(t, 0, 0, 5, t.getHeight());
		tr[1][1] = new TextureRegion(t, 5, 0, 11, t.getHeight());
		tr[1][2] = new TextureRegion(t, 16, 0, 5, t.getHeight());
		
		this.xSize = xSize;
		ySize = t.getHeight();
		
		font = FontRenderer.getFont("SANS_14");
		
		adf = new AdvancedTextEnterField() {
			@Override
			protected void specialKey(int id) {
				if(id == AdvancedTextEnterField.BUTTON_ENTER){
					unClicked();
					textEnteredDirectly(adf.text);
				}
			}
			
			@Override
			protected boolean isSpecialChar(char c) {
				return false;
			}
			
			@Override
			public void keyTyped(InputEvent e) {
				super.keyTyped(e);
				typed();
			}
		};
	}
	
	/**
	 * Will be called if Text changed
	 */
	protected void typed(){}
	
	/**
	 * Will be called if Text was Entered or focus changes
	 * @param s the text
	 */
	protected void textEntered(String s){}
	
	/**
	 * Will be called if Text was Entered by pressing Enter
	 * @param s the text
	 */
	protected void textEnteredDirectly(String s){}

	@Override
	protected void drawIntern(SpriteBatch sp, int xOff, int yOff) {
		if(!isVisible)
			return;
		
		
		if(!isDisabled)
			sp.setColor(backColor);
		else
			sp.setColor(Color.GRAY);
		sp.draw(tr[1][0], xPos+xOff, yPos+yOff);
		sp.draw(tr[1][1], xPos+xOff+5, yPos+yOff, xSize-10, ySize);
		sp.draw(tr[1][2], xPos+xOff+xSize-5, yPos+yOff);
		
		sp.setColor(Color.WHITE);
		
		sp.draw(tr[0][0], xPos+xOff, yPos+yOff);
		sp.draw(tr[0][1], xPos+xOff+5, yPos+yOff, xSize-10, ySize);
		sp.draw(tr[0][2], xPos+xOff+xSize-5, yPos+yOff);
	}

	@Override
	protected void drawTextIntern(SpriteBatch sp, int xOff, int yOff) {
		if(!isVisible)
			return;
		
		String q = transformPW();
		if(q.compareTo(lastADF) != 0 || lastXsizeTO != xSize)
			reorientText();
		
		if(!isDisabled)
			sp.setColor(backColor);
		else
			sp.setColor(Color.GRAY);
		sp.fillRect(xOff+xPos+3, yOff+yPos+3, xSize-6, ySize-6);
		
		sp.setColor(textColor);
		if(isActive && (System.currentTimeMillis()/500)%2 == 0){
			sp.fillRect(xPos+xOff+1+font.getStringWidthSpecial(q.substring(0, adf.tebpos), 'l')
					+textOffset, yPos+yOff+4, 1, font.getStringHeight());
		}
		
		font.render(sp, q, xPos+xOff+textOffset, yPos+yOff+16);
		
		sp.setColor(Color.WHITE);
		
		if(isFocused && tt != null)
			ToolTip.add(tt);
	}

	@Override
	protected void clicked() {
		isClicked();
		setKeyboardFocus();
	}
	
	public void setKeyboardFocus(){
		if(canTextEnter){
			isActive = true;
			main.InputHandler.listener = adf;
		}
	}
	
	protected void isClicked(){
		
	}
	
	@Override
	protected void unClicked() {
		if(ignoreNextRelease){
			ignoreNextRelease = false;
		}else if(isActive){
			isActive = false;
			if(main.InputHandler.listener == adf)
				main.InputHandler.listener = null;
			textEntered(adf.text);
		}
	}

	public void setText(String text){
		if(text == null)text = "";
		adf.text = text;
		adf.tebpos = text.length();
		reorientText();
	}
	
	public String getText(){
		return adf.text;
	}
	
	public void setCanTextEnter(boolean canTextEnter) {
		this.canTextEnter = canTextEnter;
	}
	
	/**
	 * Will fetch an integer from the text. If the current text isn't convertible it is colored red.
	 * @return the text as int or 0
	 */
	public int fetchInt(){
		int u = 0;
		try {
			if(changeTextByFetch)
				setText(utility.MathUtility.convertMathStringToInt(getText()));
			u = Integer.parseInt(getText());
			textColor = Color.BLACK;
		} catch (Exception e) {
			textColor = Color.RED;
		}
		return u;
	}
	
	/**
	 * Will fetch an float from the text. If the current text isn't convertible it is colored red.
	 * @return the text as float or 0
	 */
	public float fetchFloat(){
		float u = 0;
		try {
			if(changeTextByFetch)
				setText(utility.MathUtility.convertMathStringToFloat(getText()));
			u = Float.parseFloat(getText());
			textColor = Color.BLACK;
		} catch (Exception e) {
			textColor = Color.RED;
		}
		return u;
	}
	
	public void setVisible(boolean isVisible){
		this.isVisible = isVisible;
	}
	
	public void setTextBinding(BindText.BINDING textBinding) {
		this.textBinding = textBinding;
		reorientText();
	}
	
	//@Override
	//public void closeListener() {
		//unClicked();
	//}
	
	private void reorientText(){
		textOffset = BindText.getTextZeroPosition(transformPW(), font, xSize, textBinding);
		lastADF = transformPW();
		lastXsizeTO = xSize;
	}
	
	public void setTextcolor(Color c){
		textColor = c;
	}
	
	public boolean isPassword() {
		return isPassword;
	}
	
	public void setPassword() {
		isPassword = true;
	}
	
	private String transformPW(){
		if(!isPassword){
			if(adf.text.length() == 0 && overlayText != null && !isActive)
				return overlayText;
			return adf.text;
		}
		
		String s = "";
		for (int i = 0; i < adf.text.length(); i++) {
			s += "*";
		}
		return s;
	}
	
	@Override
	public void destroy() {
		adf.text = "";
	}
	
	public void setDisabled(boolean dis){
		isDisabled = dis;
	}
	
	public boolean isDisabled(){
		return isDisabled;
	}
	
	public void setOverlayText(String overlayText) {
		this.overlayText = overlayText;
	}
	
	public void setColor(Color c){
		backColor = c;
	}
	
	public void setToolTipText(String ttt){
		if(tt == null){
			tt = new ToolTip();
			tt.obeyMouse = true;
		}
		
		tt.setText(ttt);
	}
	
	public FontRenderer getFont() {
		return font;
	}
}
