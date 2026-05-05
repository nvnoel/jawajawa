package com.bda.controller;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IControllerListener extends IInterface {
    void onKeyEvent(KeyEvent keyEvent) throws RemoteException;

    void onMotionEvent(MotionEvent motionEvent) throws RemoteException;

    void onStateEvent(StateEvent stateEvent) throws RemoteException;

    public static abstract class Stub extends Binder implements IControllerListener {
        private static final String DESCRIPTOR = "com.bda.controller.IControllerListener";
        static final int TRANSACTION_onKeyEvent = 1;
        static final int TRANSACTION_onMotionEvent = 2;
        static final int TRANSACTION_onStateEvent = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IControllerListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IControllerListener)) {
                return (IControllerListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        /*  JADX ERROR: ArrayIndexOutOfBoundsException in pass: ۥ۟ۡۤ۟
            java.lang.ArrayIndexOutOfBoundsException: length=2; index=54
            */
        @Override // android.os.Binder
        public boolean onTransact(int r4, android.os.Parcel r5, android.os.Parcel r6, int r7) throws android.os.RemoteException { return false;
            /*
                r3 = this;
                r1 = 1
                switch(r4) {
                    case 1: goto Lf;
                    case 2: goto L2b;
                    case 3: goto L47;
                    case 1598968902: goto L9;
                    default: goto L4;
                }
            L4:
                boolean r1 = super.onTransact(r4, r5, r6, r7)
            L8:
                return r1
            L9:
                java.lang.String r2 = "com.bda.controller.IControllerListener"
                r6.writeString(r2)
                goto L8
            Lf:
                java.lang.String r2 = "com.bda.controller.IControllerListener"
                r5.enforceInterface(r2)
                int r2 = r5.readInt()
                if (r2 == 0) goto L29
                android.os.Parcelable$Creator<com.bda.controller.KeyEvent> r2 = com.bda.controller.KeyEvent.CREATOR
                java.lang.Object r0 = r2.createFromParcel(r5)
                com.bda.controller.KeyEvent r0 = (com.bda.controller.KeyEvent) r0
            L22:
                r3.onKeyEvent(r0)
                r6.writeNoException()
                goto L8
            L29:
                r0 = 0
                goto L22
            L2b:
                java.lang.String r2 = "com.bda.controller.IControllerListener"
                r5.enforceInterface(r2)
                int r2 = r5.readInt()
                if (r2 == 0) goto L45
                android.os.Parcelable$Creator<com.bda.controller.MotionEvent> r2 = com.bda.controller.MotionEvent.CREATOR
                java.lang.Object r0 = r2.createFromParcel(r5)
                com.bda.controller.MotionEvent r0 = (com.bda.controller.MotionEvent) r0
            L3e:
                r3.onMotionEvent(r0)
                r6.writeNoException()
                goto L8
            L45:
                r0 = 0
                goto L3e
            L47:
                java.lang.String r2 = "com.bda.controller.IControllerListener"
                r5.enforceInterface(r2)
                int r2 = r5.readInt()
                if (r2 == 0) goto L61
                android.os.Parcelable$Creator<com.bda.controller.StateEvent> r2 = com.bda.controller.StateEvent.CREATOR
                java.lang.Object r0 = r2.createFromParcel(r5)
                com.bda.controller.StateEvent r0 = (com.bda.controller.StateEvent) r0
            L5a:
                r3.onStateEvent(r0)
                r6.writeNoException()
                goto L8
            L61:
                r0 = 0
                goto L5a
            */
            // throw new UnsupportedOperationException("Method not decompiled: com.bda.controller.IControllerListener.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements IControllerListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.bda.controller.IControllerListener
            public void onKeyEvent(KeyEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(1);
                        event.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.bda.controller.IControllerListener
            public void onMotionEvent(MotionEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(1);
                        event.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.bda.controller.IControllerListener
            public void onStateEvent(StateEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(1);
                        event.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}