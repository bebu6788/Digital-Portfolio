QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

CONFIG += c++17

# You can make your code fail to compile if it uses deprecated APIs.
# In order to do so, uncomment the following line.
#DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

SOURCES += \
    boardsquare.cpp \
    instructionsdialog.cpp \
    main.cpp \
    gamewindow.cpp \
    numplayersdialog.cpp \
    player.cpp \
    powerupdialog.cpp \
    structure.cpp \
    welcomedialog.cpp

HEADERS += \
    boardsquare.h \
    gamewindow.h \
    instructionsdialog.h \
    numplayersdialog.h \
    player.h \
    powerupdialog.h \
    structure.h \
    welcomedialog.h

FORMS += \
    gamewindow.ui \
    instructionsdialog.ui \
    numplayersdialog.ui \
    powerupdialog.ui \
    welcomedialog.ui

# Default rules for deployment.
qnx: target.path = /tmp/$${TARGET}/bin
else: unix:!android: target.path = /opt/$${TARGET}/bin
!isEmpty(target.path): INSTALLS += target

RESOURCES += \
  resources.qrc
