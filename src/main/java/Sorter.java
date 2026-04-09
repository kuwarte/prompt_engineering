public class Sorter {

    private final Dashboard dashboard;
    private final String criteria;

    public Sorter(Dashboard dashboard, String criteria) {
        this.dashboard = dashboard;
        this.criteria = criteria;
    }

    public void mergeSort(Product[] arr, int left, int right) {
        if (left >= right) return;

        int mid = (left + right) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private void merge(Product[] arr, int left, int mid, int right) {
        dashboard.showMergeStep(arr, left, mid, right);

        Product[] leftArr  = new Product[mid - left + 1];
        Product[] rightArr = new Product[right - mid];

        System.arraycopy(arr, left,      leftArr,  0, leftArr.length);
        System.arraycopy(arr, mid + 1,   rightArr, 0, rightArr.length);

        int i = 0, j = 0, k = left;

        while (i < leftArr.length && j < rightArr.length) {
            dashboard.showComparison(arr, leftArr[i], rightArr[j], left, right);

            if (leftArr[i].getValue(criteria) <= rightArr[j].getValue(criteria)) {
                arr[k++] = leftArr[i++];
            } else {
                arr[k++] = rightArr[j++];
            }

            dashboard.renderBars(arr, left, right, k - 1);
            sleep(180);
        }

        while (i < leftArr.length) { arr[k++] = leftArr[i++]; dashboard.renderBars(arr, left, right, k-1); sleep(100); }
        while (j < rightArr.length){ arr[k++] = rightArr[j++]; dashboard.renderBars(arr, left, right, k-1); sleep(100); }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
