class Request {
    int start;
    int end;
    int direction; // 1 - up, -1 - down;
    Request(int start, int end, int direction) {
        this.start = start;
        this.end = end;
        this.direction = direction;
    }
}