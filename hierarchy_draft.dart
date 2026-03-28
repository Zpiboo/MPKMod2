abstract class ComponentHolder<C extends Component> {
  List<C> getChildren();

  void addChild(C child);
  void removeChild(C child);
}

class Root<C extends Component> {
  List<C> _children;
  List<C> getChildren() { return _children; }
}

class Component {
  Root<Component> _mainRoot;

  void addChild(Component child) {
    _mainRoot.addChild(child);
  }
}
