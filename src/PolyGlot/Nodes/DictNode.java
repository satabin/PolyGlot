/*
 * Copyright (c) 2014-2017, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: Creative Commons Attribution-NonCommercial 4.0 International Public License
 * See LICENSE.TXT included with this code to read the full license agreement.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
// This is the type which all nodes and storage types extend.
package PolyGlot.Nodes;

import PolyGlot.CustomControls.PAlphaMap;

/**
 *
 * @author draque
 */
public abstract class DictNode implements Comparable<DictNode> {

    // this represents the primary string value of the node, whether it is
    // a word, a declension type, etc.

    protected String value = "";

    protected Integer id = 0;

    // used for alphabetic ordering of nodes
    //private Map<Character, Integer> alphaOrder = new HashMap<>();
    private PAlphaMap<String, Integer> alphaOrder = new PAlphaMap<>();

    /**
     * Sets a node equal to the argument node
     *
     * @param _node Node to set all values equal to.
     */
    abstract public void setEqual(DictNode _node) throws ClassCastException;

    public void setId(Integer _id) {
        id = _id;
    }

    public Integer getId() {
        return id;
    }

    public void setAlphaOrder(PAlphaMap _alphaOrder) {
        alphaOrder = _alphaOrder;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String conWord) {
        this.value = conWord.trim();
    }

    /**
     * implements compareTo in way that custom alpha sorting may be used
     *
     * @param _compare value to compare to this one
     * @return
     */
    @Override
    public int compareTo(DictNode _compare) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        final String comp = _compare.getValue();
        final String me = this.getValue();
        int ret;

        if (comp.equals(me)) {
            ret = EQUAL;
        } else if (comp.equals("")) {
            ret = AFTER;
        } else if (me.equals("")) {
            ret = BEFORE;
        } else {
            if (alphaOrder.isEmpty()) {
                // if no settings,or missing settings for given character, comparison always goes before
                ret = BEFORE;
            } else {
                // compare values based on largest front facing clusters found in alphabet order
                int longest = alphaOrder.getLongestEntry();
                int meLen = me.length();
                int compLen = comp.length();
                int meAlpha = -1;
                int compAlpha = -1;

                for (int i = meLen > longest ? longest : meLen; i >= 0; i--) {
                    String mePrefix = me.substring(0, i);

                    if (alphaOrder.containsKey(mePrefix)) {
                        meAlpha = (int) alphaOrder.get(mePrefix);
                        break;
                    }
                }

                if (meAlpha == -1) {
                    // no prefixed pattern found for this value: default to system alpha order
                    ret = this.getValue().compareToIgnoreCase(_compare.getValue());
                } else {
                    int preLen = 0;

                    for (int i = compLen > longest ? longest : compLen; i >= 0; i--) {
                        String compPrefix = comp.substring(0, i);

                        if (alphaOrder.containsKey(compPrefix)) {
                            compAlpha = (int) alphaOrder.get(compPrefix);
                            preLen = compPrefix.length(); // record length for substring truncation if current patterns are equal
                            break;
                        }
                    }

                    if (compAlpha == -1) { 
                        // no prefixed pattern found for comp value: default to system alpha order
                        ret = this.getValue().compareToIgnoreCase(_compare.getValue());
                    } else if (compAlpha > meAlpha) {
                        ret = BEFORE;
                    } else if (compAlpha < meAlpha) {
                        ret = AFTER;
                    } else {
                        // two patterns are the same. Truncate and check subpatterns
                        ConWord compChild = new ConWord();
                        ConWord thisChild = new ConWord();

                        compChild.setAlphaOrder(alphaOrder);
                        thisChild.setAlphaOrder(alphaOrder);

                        compChild.setValue(_compare.getValue().substring(preLen));
                        thisChild.setValue(this.getValue().substring(preLen));

                        ret = thisChild.compareTo(compChild);
                    }
                }
            }
        }

        return ret;
    }

    @Override
    public String toString() {
        return value.equals("") ? " " : value;
    }
}
