package compsci.labs.letterinventory;

public class LetterInventory {
    private final int[] counts = new int[26];
    private int totalCount = 0;

    public LetterInventory(String data) {
        if (data == null) return;
        for (int i = 0, n = data.length(); i < n; i++) {
            char ch = data.charAt(i);
            if (!Character.isLetter(ch)) continue;
            int idx = index(Character.toLowerCase(ch));
            counts[idx]++;
            totalCount++;
        }
    }

    private LetterInventory(int[] counts, int totalCount) {
        this.totalCount = totalCount;
        System.arraycopy(counts, 0, this.counts, 0, 26);
    }

    private int index(char lower) {
        return lower - 'a';
    }

    private void checkLetter(char letter) {
        if (!Character.isLetter(letter)) {
            throw new IllegalArgumentException("Key must be a letter");
        }
    }

    public int get(char letter) {
        checkLetter(letter);
        return counts[index(Character.toLowerCase(letter))];
    }

    public void set(char letter, int value) {
        checkLetter(letter);
        if (value < 0) throw new IllegalArgumentException("Value cannot be negative");
        int idx = index(Character.toLowerCase(letter));
        totalCount += value - counts[idx];
        counts[idx] = value;
    }

    public int size() {
        return totalCount;
    }

    public boolean isEmpty() {
        return totalCount == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(totalCount + 2);
        sb.append('[');
        for (int i = 0; i < 26; i++) {
            for (int k = 0, c = counts[i]; k < c; k++) sb.append((char) ('a' + i));
        }
        sb.append(']');
        return sb.toString();
    }

    public LetterInventory add(LetterInventory other) {
        if (other == null) return new LetterInventory(this.counts, this.totalCount);
        int[] newCounts = new int[26];
        int newTotal = 0;
        for (int i = 0; i < 26; i++) {
            newCounts[i] = this.counts[i] + other.counts[i];
            newTotal += newCounts[i];
        }
        return new LetterInventory(newCounts, newTotal);
    }

    public LetterInventory subtract(LetterInventory other) {
        if (other == null) return new LetterInventory(this.counts, this.totalCount);
        int[] newCounts = new int[26];
        int newTotal = 0;
        for (int i = 0; i < 26; i++) {
            int v = this.counts[i] - other.counts[i];
            if (v < 0) return null;
            newCounts[i] = v;
            newTotal += v;
        }
        return new LetterInventory(newCounts, newTotal);
    }
}
