package seng301.assn2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.lsmr.vending.frontend2.Coin;
import org.lsmr.vending.frontend2.hardware.AbstractHardware;
import org.lsmr.vending.frontend2.hardware.AbstractHardwareListener;
import org.lsmr.vending.frontend2.hardware.CapacityExceededException;
import org.lsmr.vending.frontend2.hardware.CoinRack;
import org.lsmr.vending.frontend2.hardware.CoinSlot;
import org.lsmr.vending.frontend2.hardware.CoinSlotListener;
import org.lsmr.vending.frontend2.hardware.DisabledException;
import org.lsmr.vending.frontend2.hardware.Display;
import org.lsmr.vending.frontend2.hardware.EmptyException;
import org.lsmr.vending.frontend2.hardware.PopCanRack;
import org.lsmr.vending.frontend2.hardware.SelectionButton;
import org.lsmr.vending.frontend2.hardware.SelectionButtonListener;
import org.lsmr.vending.frontend2.hardware.SimulationException;
import org.lsmr.vending.frontend2.hardware.VendingMachine;

/**
 * Represents the logic of the vending machine, i.e. its software.
 * 
 * @author Robert J. Walker
 */
public class VendingMachineLogic implements CoinSlotListener, SelectionButtonListener {
    private int availableFunds = 0;
    private VendingMachine vendingMachine;
    private Map<SelectionButton, Integer> buttonToIndex = new HashMap<>();
    private Map<Integer, Integer> valueToIndexMap = new HashMap<>();

    /**
     * Basic constructor.
     * 
     * @param vm
     *            The vending machine in which to install the logic. Cannot be
     *            null.
     * @throws NullPointerException
     *             If the argument is null.
     */
    public VendingMachineLogic(VendingMachine vm) {
	vendingMachine = vm;

	vm.getCoinSlot().register(this);
	for(int i = 0; i < vm.getNumberOfSelectionButtons(); i++) {
	    SelectionButton sb = vm.getSelectionButton(i);
	    sb.register(this);
	    buttonToIndex.put(sb, i);
	}

	for(int i = 0; i < vm.getNumberOfCoinRacks(); i++) {
	    int value = vm.getCoinKindForCoinRack(i);
	    valueToIndexMap.put(value, i);
	}
    }

    @Override
    public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
    }

    @Override
    public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
    }

    @Override
    public void validCoinInserted(CoinSlot coinSlotSimulator, Coin coin) {
	availableFunds += coin.getValue();
    }

    @Override
    public void coinRejected(CoinSlot coinSlotSimulator, Coin coin) {
    }

    @Override
    public void pressed(SelectionButton button) {
	Integer index = buttonToIndex.get(button);

	if(index == null)
	    throw new SimulationException("An invalid selection button was pressed");

	int cost = vendingMachine.getPopKindCost(index);

	if(cost <= availableFunds) {
	    PopCanRack pcr = vendingMachine.getPopCanRack(index);
	    if(pcr.size() > 0) {
		try {
		    pcr.dispensePopCan();
		    vendingMachine.getCoinReceptacle().storeCoins();
		    availableFunds = deliverChange(cost, availableFunds);
		}
		catch(DisabledException | EmptyException | CapacityExceededException e) {
		    throw new SimulationException(e);
		}
	    }
	}
	else {
	    Display disp = vendingMachine.getDisplay();
	    disp.display("Cost: " + cost + "; available funds: " + availableFunds);
	    final Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
		@Override
		public void run() {
		    timer.cancel();
		}
	    }, 5000);
	}
    }

    private Map<Integer, List<Integer>> changeHelper(ArrayList<Integer> values, int index, int changeDue) {
	if(index >= values.size())
	    return null;

	int value = values.get(index);
	Integer ck = valueToIndexMap.get(value);
	CoinRack cr = vendingMachine.getCoinRack(ck);

	Map<Integer, List<Integer>> map = changeHelper(values, index + 1, changeDue);

	if(map == null) {
	    map = new TreeMap<>(new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
		    return o2 - o1;
		}
	    });
	    map.put(0, new ArrayList<Integer>());
	}

	Map<Integer, List<Integer>> newMap = new TreeMap<>(map);
	for(Integer total : map.keySet()) {
	    List<Integer> res = map.get(total);

	    for(int count = cr.size(); count >= 0; count--) {
		int newTotal = count * value + total;
		if(newTotal <= changeDue) {
		    List<Integer> newRes = new ArrayList<>();
		    if(res != null)
			newRes.addAll(res);

		    for(int i = 0; i < count; i++)
			newRes.add(ck);

		    newMap.put(newTotal, newRes);
		}
	    }
	}

	return newMap;
    }

    private int deliverChange(int cost, int entered) throws CapacityExceededException, EmptyException, DisabledException {
	int changeDue = entered - cost;

	if(changeDue < 0)
	    throw new InternalError("Cost was greater than entered, which should not happen");

	ArrayList<Integer> values = new ArrayList<>();
	for(Integer ck : valueToIndexMap.keySet())
	    values.add(ck);

	Map<Integer, List<Integer>> map = changeHelper(values, 0, changeDue);

	List<Integer> res = map.get(changeDue);
	if(res == null) {
	    // An exact match was not found, so do your best
	    Iterator<Integer> iter = map.keySet().iterator();
	    Integer max = 0;
	    while(iter.hasNext()) {
		Integer temp = iter.next();
		if(temp > max)
		    max = temp;
	    }
	    res = map.get(max);
	}

	for(Integer ck : res) {
	    CoinRack cr = vendingMachine.getCoinRack(ck);
	    cr.releaseCoin();
	    changeDue -= vendingMachine.getCoinKindForCoinRack(ck);
	}

	return changeDue;
    }

}
